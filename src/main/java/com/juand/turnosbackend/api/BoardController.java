package com.juand.turnosbackend.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class BoardController {

  @MessageMapping("/sendBoard")
  @SendTo("/topic/server")
  public String processMessage(String mensaje){
    return "Servidor dice: " + mensaje;
  }
}
