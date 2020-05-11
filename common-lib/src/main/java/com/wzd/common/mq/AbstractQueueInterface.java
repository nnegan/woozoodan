package com.wzd.common.mq;

import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public abstract interface AbstractQueueInterface {

	public ResponseEntity asyncCall(Object request)
			throws InterruptedException, ExecutionException, UnsupportedEncodingException;

	public ResponseEntity syncCall(Object request)
			throws InterruptedException, ExecutionException, UnsupportedEncodingException;

}
