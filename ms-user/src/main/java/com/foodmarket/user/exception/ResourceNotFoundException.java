package com.foodmarket.user.exception;
/** Excepcion para "VALORES" no encontrados = 404 */
public class ResourceNotFoundException extends RuntimeException { public ResourceNotFoundException(String m) { super(m); } }
