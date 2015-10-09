/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.agent.firealarm.virtual.core;

/**
 * A Configuration class that holds all the Agent specific details that are read from the
 * 'deviceConfig.properties' file. This file is generated by the IoT-Server at the time of
 * downloading the device agent from the IoT-Server.
 */
public class AgentConfiguration {
	private String deviceOwner;
	private String deviceId;
	private String iotServerEP;
	private String iotServerServiceEP;
	private String apimGatewayEP;
	private String mqttBrokerEP;
	private String xmppServerEP;
	private String authMethod;
	private String authToken;
	private String refreshToken;
	private String networkInterface;
	private int dataPushInterval;

	public String getDeviceOwner() {
		return deviceOwner;
	}

	public void setDeviceOwner(String deviceOwner) {
		this.deviceOwner = deviceOwner;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getIotServerEP() {
		return iotServerEP;
	}

	public void setIotServerEP(String iotServerEP) {
		this.iotServerEP = iotServerEP;
	}

	public String getIotServerServiceEP() {
		return iotServerServiceEP;
	}

	public void setIotServerServiceEP(String iotServerServiceEP) {
		this.iotServerServiceEP = iotServerServiceEP;
	}

	public String getApimGatewayEP() {
		return apimGatewayEP;
	}

	public void setApimGatewayEP(String apimGatewayEP) {
		this.apimGatewayEP = apimGatewayEP;
	}

	public String getMqttBrokerEP() {
		return mqttBrokerEP;
	}

	public void setMqttBrokerEP(String mqttBrokerEP) {
		this.mqttBrokerEP = mqttBrokerEP;
	}

	public String getXmppServerEP() {
		return xmppServerEP;
	}

	public void setXmppServerEP(String xmppServerEP) {
		this.xmppServerEP = xmppServerEP;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getNetworkInterface() {
		return networkInterface;
	}

	public void setNetworkInterface(String networkInterface) {
		this.networkInterface = networkInterface;
	}

	public int getDataPushInterval() {
		return dataPushInterval;
	}

	public void setDataPushInterval(int dataPushInterval) {
		this.dataPushInterval = dataPushInterval;
	}
}


