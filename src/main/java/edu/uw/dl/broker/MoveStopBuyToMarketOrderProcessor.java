///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package edu.uw.dl.broker;
//
//import edu.uw.ext.framework.broker.OrderQueue;
//import edu.uw.ext.framework.order.Order;
//import edu.uw.ext.framework.order.StopBuyOrder;
//import java.util.function.Consumer;
//
///**
// *
// * @author dixya
// */
//public class MoveStopBuyToMarketOrderProcessor implements Consumer<StopBuyOrder>{
//private final OrderQueue<Boolean,Order> marketQ;
//public MoveStopBuyToMarketOrderProcessor(OrderQueue<Boolean,Order> marketQ){
//    this.marketQ=marketQ;
//}
//    @Override
//    public void accept(StopBuyOrder t) {
//        marketQ.enqueue(t);
//    }
//    
//}
