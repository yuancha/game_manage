<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<div class="sidebar-menu toggle-others fixed" style="">
                
                <div class="sidebar-menu-inner ps-container ps-active-y">	
                    
                    <header class="logo-env">
                        
                        <!-- logo -->
                        <div class="logo">
                            <a href="#" class="logo-expanded">
                                <h4 style="color: #fff">六六游戏集装箱</h4>
                            </a>
                            
                            <a href="#" class="logo-collapsed">
                               	<h6 style="color: #fff">六六游戏集装箱</h6>
                            </a>
                        </div>
                        
                        <!-- This will toggle the mobile menu and will be visible only on mobile devices -->
                        <div class="mobile-menu-toggle visible-xs">
                           <!--  <a href="#" data-toggle="user-info-menu">
                                <i class="fa-bell-o"></i>
                                <span class="badge badge-success">7</span>
                            </a> -->
                            
                            <a href="#" data-toggle="mobile-menu" style="background: #fff;width: 20px;">
                                <i class="fa-bars"></i>
                            </a>
                        </div>
                  
                                    
                    </header>
                            
                    <ul id="main-menu" class="main-menu">
                    	<c:forEach items="${gameOpens}" var="game">  
						    <li>
                            	<a href="#" id='${game.gameId}'>
                                	<span class="title">${game.name}</span>
                            	</a>
                        	</li>
						</c:forEach>
                    </ul>
                            
                    <div class="ps-scrollbar-x-rail" style="display: block; width: 340px; left: 0px; bottom: 3px;">
                        <div class="ps-scrollbar-x" style="left: 0px; width: 0px;"></div>
                    </div>
                    <div class="ps-scrollbar-y-rail" style="display: inherit; top: 0px; height: 930px; right: 2px;">
                        <div class="ps-scrollbar-y" style="top: 0px; height: 777px;"></div>
                    </div>
                </div>
                
            </div>