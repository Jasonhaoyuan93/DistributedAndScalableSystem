package edu.neu.cs6650;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6650.model.Request;
import edu.neu.cs6650.model.Response;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

@WebServlet(name = "org.neu.cs6650.server.TwinderServlet", value = "/org.neu.cs6650.server.TwinderServlet")
public class TwinderServlet extends HttpServlet {

  private static final String INVALID_PATH_MESSAGE = "invalid path";

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    handleError(response,HttpServletResponse.SC_NOT_FOUND, new Response(INVALID_PATH_MESSAGE));
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
        Request body = objectMapper.readValue(stringBuilder.toString(), Request.class);
        isValidRequest(body,response);
      }catch (JsonProcessingException e){
        handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response(e.getMessage()));
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

  private void handleError(HttpServletResponse response, int httpStatus, Object payload) throws IOException {
    response.setContentType("application/json");
    response.setStatus(httpStatus);
    response.getWriter().write(objectMapper.writeValueAsString(payload));
  }

  private void isValidRequest(Request request, HttpServletResponse response) throws IOException {
    if(StringUtils.isBlank(request.getSwipee())||!StringUtils.isNumeric(request.getSwipee())
    || Integer.parseInt(request.getSwipee())>1000000||Integer.parseInt(request.getSwipee())<=0){
      handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response("Invalid Swipee ID."));
      return;
    }
    if(StringUtils.isBlank(request.getSwiper())||!StringUtils.isNumeric(request.getSwiper())
    || Integer.parseInt(request.getSwiper())>5000||Integer.parseInt(request.getSwiper())<=0){
      handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response("Invalid Swiper ID."));
      return;
    }
    if(StringUtils.isBlank(request.getComment())){
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getWriter().write(objectMapper.writeValueAsString(request));
      return;
    }
    if(request.getComment().length()>256){
      handleError(response, HttpServletResponse.SC_BAD_REQUEST, new Response("Message length exceed 256."));
      return;
    }
    response.setStatus(HttpServletResponse.SC_CREATED);
    response.getWriter().write(objectMapper.writeValueAsString(request));
  }
}
