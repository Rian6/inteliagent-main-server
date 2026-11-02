package com.inteliagent.main_server.service;

public class RoutingBusyException extends RuntimeException {
    public RoutingBusyException() { super("Otimização temporariamente ocupada"); }
}
