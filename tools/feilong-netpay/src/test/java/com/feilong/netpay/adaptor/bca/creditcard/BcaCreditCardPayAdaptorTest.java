/**
 * Copyright (C) 2008 feilong (venusdrogon@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.netpay.adaptor.bca.creditcard;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.feilong.netpay.adaptor.BasePaymentTest;
import com.feilong.netpay.adaptor.PaymentAdaptor;
import com.feilong.netpay.command.QueryRequest;
import com.feilong.tools.net.httpclient.HttpClientUtilException;

/**
 * @author <a href="mailto:venusdrogon@163.com">金鑫</a>
 * @version 1.0 Mar 24, 2014 11:45:24 AM
 */
public class BcaCreditCardPayAdaptorTest extends BasePaymentTest{

	private static final Logger	log	= LoggerFactory.getLogger(BcaCreditCardPayAdaptorTest.class);

	@Autowired
	@Qualifier("bcaCreditCardPayAdaptor")
	private PaymentAdaptor		paymentAdaptor;

	@Test
	public final void createPaymentForm(){
		Map<String, String> specialSignMap = null;
		createPaymentForm(paymentAdaptor, specialSignMap);
	}

	@Test
	public final void getQueryResult() throws HttpClientUtilException{

		BcaCreditCardPayAdaptor bcaCreditCardPayAdaptor = (BcaCreditCardPayAdaptor) paymentAdaptor;
		QueryRequest queryRequest = new QueryRequest();
		queryRequest.setTradeNo("010003170001");
		
		
		bcaCreditCardPayAdaptor.getQueryResult(queryRequest);
	}

}
