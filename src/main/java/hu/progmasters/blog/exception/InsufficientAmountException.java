package hu.progmasters.blog.exception;

import lombok.Getter;

@Getter
public class InsufficientAmountException extends RuntimeException {

    private final int price;
    public InsufficientAmountException(int price) {
        this.price = price;
    }
}
