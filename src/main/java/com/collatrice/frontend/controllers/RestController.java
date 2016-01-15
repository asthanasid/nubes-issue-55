/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.collatrice.frontend.controllers;

import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author Siddhartha
 */
@Controller("/data")
public class RestController {
    
    @GET("/dashboard")
    public String getDashboard(){
        System.out.println("com.collatrice.frontend.controllers.RestController.getDashbaord()");
        return "hello";
    }
    
}
