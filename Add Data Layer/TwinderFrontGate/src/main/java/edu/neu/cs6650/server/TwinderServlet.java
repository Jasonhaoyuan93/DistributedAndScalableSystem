package edu.neu.cs6650.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.server.model.Request;
import edu.neu.cs6650.server.model.Response;
import edu.neu.cs6650.server.service.RMQPublishService;
import edu.neu.cs6650.server.service.TwinderMatchService;
import edu.neu.cs6650.server.service.TwinderStatsService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "edu.neu.cs6650.server.TwinderServlet", value = "/edu.neu.cs6650.server.TwinderServlet")
public class TwinderServlet extends HttpServlet {

  private static final String INVALID_PATH_MESSAGE = "invalid path";

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RMQPublishService rmqPublishService;
  private final TwinderMatchService twinderMatchService;
  private final TwinderStatsService twinderStatsService;

  public TwinderServlet() throws IOException, TimeoutException {
    twinderMatchService = new TwinderMatchService();
    twinderStatsService = new TwinderStatsService();
    rmqPublishService = new RMQPublishService();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      handleError(response, HttpServletResponse.SC_NOT_FOUND, new Response(INVALID_PATH_MESSAGE));
      return;
    }

    String[] urlParts = urlPath.split("/");

    Route route = obtainGetRoute(urlParts);
    if(route == Route.UNKNOWN){
      handleError(response,HttpServletResponse.SC_NOT_FOUND, new Response(INVALID_PATH_MESSAGE));
      return;
    }


    try{
      int uid = Integer.parseInt(urlParts[2]);
      response.setContentType("application/json");

      switch(route){
        case MATCH -> {
          twinderMatchService.findMatches(uid, response);
        }
        case STATS -> {
          twinderStatsService.obtainStatsWithUID(uid, response);
        }
      }
    }catch(NumberFormatException e){
      handleError(response,HttpServletResponse.SC_BAD_REQUEST, new Response("Malformed user ID"));
    }


  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      handleError(response, HttpServletResponse.SC_NOT_FOUND, new Response(INVALID_PATH_MESSAGE));
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isSwipeRoute(urlParts)) {
      handleError(response,HttpServletResponse.SC_NOT_FOUND, new Response(INVALID_PATH_MESSAGE));
    } else {
      // do any sophisticated processing with urlParts which contains all the url params
      StringBuilder stringBuilder = new StringBuilder();
      BufferedReader bodyReader = request.getReader();
      while(bodyReader.ready()){
        stringBuilder.append(bodyReader.readLine());
      }
      try{
        Request requestBody = objectMapper.readValue(stringBuilder.toString(), Request.class);
        requestBody.setSwipeRight("right".equalsIgnoreCase(urlParts[2]));

        if(!isValidPostRequest(requestBody,response)){
          return;
        }
        rmqPublishService.publishMessage(requestBody);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(objectMapper.writeValueAsString(requestBody));
        response.setContentType("application/json");

      }catch (JsonProcessingException e){
        handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response(e.getMessage()));
      }catch (Exception e){
        e.printStackTrace();
        handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new Response(e.getMessage()));
      }
    }
  }
  private boolean isSwipeRoute(String[] urlParts) {
    if(!"swipe".equalsIgnoreCase(urlParts[1])){
      return false;
    }
    if(!"left".equalsIgnoreCase(urlParts[2]) && !"right".equalsIgnoreCase(urlParts[2])){
      return false;
    }
    return true;
  }

  private Route obtainGetRoute(String[] urlParts){
    return switch (urlParts[1].toLowerCase()){
      case "stats"->Route.STATS;
      case "matches"->Route.MATCH;
      default -> Route.UNKNOWN;
    };
  }


  private void handleError(HttpServletResponse response, int httpStatus, Object payload) throws IOException {
    response.setContentType("application/json");
    response.setStatus(httpStatus);
    response.getWriter().write(objectMapper.writeValueAsString(payload));
  }

  private boolean isValidPostRequest(Request request, HttpServletResponse response) throws IOException {
    if(StringUtils.isBlank(request.getSwipee())||!StringUtils.isNumeric(request.getSwipee())
    || Integer.parseInt(request.getSwipee())>1000000||Integer.parseInt(request.getSwipee())<=0){
      handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response("Invalid Swipee ID."));
      return false;
    }
    if(StringUtils.isBlank(request.getSwiper())||!StringUtils.isNumeric(request.getSwiper())
    || Integer.parseInt(request.getSwiper())>5000||Integer.parseInt(request.getSwiper())<=0){
      handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response("Invalid Swiper ID."));
      return false;
    }
    if(!StringUtils.isBlank(request.getComment()) && request.getComment().length()>256){
      handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response("Message length exceed 256."));
      return false;
    }
    return true;
  }

  private int obtainUserId(String[] urlParts) {
    return Integer.parseInt(urlParts[2]);
  }
}
