package com.tp.jmsproject.pojo;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProcessedBookOrder {

//    @JsonCreator
//    public ProcessedBookOrder(
//            @JsonProperty("bookOrder") BookOrder bookOrder,
//            @JsonProperty("processingDateTime") Date processingDateTime,
//            @JsonProperty("expectedShippingDateTime") Date expectedShippingDateTime) {
//        this.bookOrder = bookOrder;
//        this.processingDateTime = processingDateTime;
//        this.expectedShippingDateTime = expectedShippingDateTime;
//    }

    private  BookOrder bookOrder;
    private  Date processingDateTime;
    private  Date expectedShippingDateTime;

//    public BookOrder getBookOrder() {
//        return bookOrder;
//    }
//
//    public Date getProcessingDateTime() {
//        return processingDateTime;
//    }
//
//    public Date getExpectedShippingDateTime() {
//        return expectedShippingDateTime;
//    }
//
//    @Override
//    public String toString() {
//        return "ProcessedBookOrder{" +
//                "bookOrder=" + bookOrder +
//                ", processingDateTime=" + processingDateTime +
//                ", expectedShippingDateTime=" + expectedShippingDateTime +
//                '}';
//    }
}
