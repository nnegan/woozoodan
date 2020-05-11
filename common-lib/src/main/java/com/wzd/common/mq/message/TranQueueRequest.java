package com.wzd.common.mq.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class TranQueueRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String svcId; // 서비스아이디

	private String txId; // 트랜잭션아이디

	private String orderNo; // 주문번호
}
