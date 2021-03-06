/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.wso2.carbon.device.mgt.iot.agent.firealarm.virtual.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.agent.firealarm.virtual.exception.AgentCoreOperationException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * This class contains all the core operations of the FireAlarm agent that are common to both
 * Virtual and Real Scenarios. These operations include, connecting to and subscribing to an MQTT
 * queue and to a XMPP Server. Pushing temperature data to the IoT-Server at timely intervals.
 * Reading device specific configuration from a configs file etc....
 */
public class AgentCoreOperations {

	private static final Log log = LogFactory.getLog(AgentCoreOperations.class);
	private static final AgentManager agentManager = AgentManager.getInstance();

	/**
	 * This method reads the agent specific configurations for the device from the
	 * "deviceConfigs.properties" file found at /repository/conf folder.
	 * If the properties file is not found in the specified path, then the configuration values
	 * are set to the default ones in the 'AgentConstants' class.
	 *
	 * @return an object of type 'AgentConfiguration' which contains all the necessary
	 * configuration attributes
	 */
	public static AgentConfiguration readIoTServerConfigs() {
		AgentConfiguration iotServerConfigs = new AgentConfiguration();
		Properties properties = new Properties();
		InputStream propertiesInputStream = null;
		String propertiesFileName = AgentConstants.AGENT_PROPERTIES_FILE_NAME;

		try {
			ClassLoader loader = AgentCoreOperations.class.getClassLoader();
			URL path = loader.getResource(propertiesFileName);
			System.out.println(path);
			String root = path.getPath().replace(
					"wso2-firealarm-virtual-agent.jar!/deviceConfig.properties",
					"").replace("jar:", "").replace("file:", "");
			propertiesInputStream = new FileInputStream(
					root + AgentConstants.AGENT_PROPERTIES_FILE_NAME);

			//load a properties file from class path, inside static method
			properties.load(propertiesInputStream);

			iotServerConfigs.setDeviceOwner(properties.getProperty(
					AgentConstants.DEVICE_OWNER_PROPERTY));
			iotServerConfigs.setDeviceId(properties.getProperty(
					AgentConstants.DEVICE_ID_PROPERTY));
			iotServerConfigs.setDeviceName(properties.getProperty(
					AgentConstants.DEVICE_NAME_PROPERTY));
			iotServerConfigs.setControllerContext(properties.getProperty(
					AgentConstants.DEVICE_CONTROLLER_CONTEXT_PROPERTY));
			iotServerConfigs.setHTTPS_ServerEndpoint(properties.getProperty(
					AgentConstants.SERVER_HTTPS_EP_PROPERTY));
			iotServerConfigs.setHTTP_ServerEndpoint(properties.getProperty(
					AgentConstants.SERVER_HTTP_EP_PROPERTY));
			iotServerConfigs.setApimGatewayEndpoint(properties.getProperty(
					AgentConstants.APIM_GATEWAY_EP_PROPERTY));
			iotServerConfigs.setMqttBrokerEndpoint(properties.getProperty(
					AgentConstants.MQTT_BROKER_EP_PROPERTY));
			iotServerConfigs.setXmppServerEndpoint(properties.getProperty(
					AgentConstants.XMPP_SERVER_EP_PROPERTY));
			iotServerConfigs.setAuthMethod(properties.getProperty(
					AgentConstants.AUTH_METHOD_PROPERTY));
			iotServerConfigs.setAuthToken(properties.getProperty(
					AgentConstants.AUTH_TOKEN_PROPERTY));
			iotServerConfigs.setRefreshToken(properties.getProperty(
					AgentConstants.REFRESH_TOKEN_PROPERTY));
			iotServerConfigs.setDataPushInterval(Integer.parseInt(properties.getProperty(
					AgentConstants.PUSH_INTERVAL_PROPERTY)));

			log.info(AgentConstants.LOG_APPENDER + "Device Owner: " +
					         iotServerConfigs.getDeviceOwner());
			log.info(AgentConstants.LOG_APPENDER + "Device ID: " + iotServerConfigs.getDeviceId());
			log.info(AgentConstants.LOG_APPENDER + "Device Name: " +
					         iotServerConfigs.getDeviceName());
			log.info(AgentConstants.LOG_APPENDER + "Device Controller Context: " +
					         iotServerConfigs.getControllerContext());
			log.info(AgentConstants.LOG_APPENDER + "IoT Server HTTPS EndPoint: " +
					         iotServerConfigs.getHTTPS_ServerEndpoint());
			log.info(AgentConstants.LOG_APPENDER + "IoT Server HTTP EndPoint: " +
					         iotServerConfigs.getHTTP_ServerEndpoint());
			log.info(AgentConstants.LOG_APPENDER + "API-Manager Gateway EndPoint: " +
					         iotServerConfigs.getApimGatewayEndpoint());
			log.info(AgentConstants.LOG_APPENDER + "MQTT Broker EndPoint: " +
					         iotServerConfigs.getMqttBrokerEndpoint());
			log.info(AgentConstants.LOG_APPENDER + "XMPP Server EndPoint: " +
					         iotServerConfigs.getXmppServerEndpoint());
			log.info(AgentConstants.LOG_APPENDER + "Authentication Method: " +
					         iotServerConfigs.getAuthMethod());
			log.info(AgentConstants.LOG_APPENDER + "Authentication Token: " +
					         iotServerConfigs.getAuthToken());
			log.info(AgentConstants.LOG_APPENDER + "Refresh Token: " +
					         iotServerConfigs.getRefreshToken());
			log.info(AgentConstants.LOG_APPENDER + "Data Push Interval: " +
					         iotServerConfigs.getDataPushInterval());

		} catch (FileNotFoundException ex) {
			log.error(AgentConstants.LOG_APPENDER + "Unable to find " + propertiesFileName +
					          " file at: " + AgentConstants.PROPERTIES_FILE_PATH);
			iotServerConfigs = setDefaultDeviceConfigs();

		} catch (IOException ex) {
			log.error(AgentConstants.LOG_APPENDER + "Error occurred whilst trying to fetch '" +
					          propertiesFileName + "' from: " +
					          AgentConstants.PROPERTIES_FILE_PATH);
			iotServerConfigs = setDefaultDeviceConfigs();

		} finally {
			if (propertiesInputStream != null) {
				try {
					propertiesInputStream.close();
				} catch (IOException e) {
					log.error(AgentConstants.LOG_APPENDER +
							          "Error occurred whilst trying to close InputStream " +
							          "resource used to read the '" + propertiesFileName +
							          "' file");
				}
			}
		}
		return iotServerConfigs;
	}

	/**
	 * Sets the default Device specific configurations listed in the 'AgentConstants' class.
	 *
	 * @return an object of AgentConfiguration class including all default device specific configs.
	 */
	private static AgentConfiguration setDefaultDeviceConfigs() {
		log.warn(AgentConstants.LOG_APPENDER +
				         "Default Values are being set to all Agent specific configurations");

		AgentConfiguration iotServerConfigs = new AgentConfiguration();

		iotServerConfigs.setDeviceOwner(AgentConstants.DEFAULT_DEVICE_OWNER);
		iotServerConfigs.setDeviceId(AgentConstants.DEFAULT_DEVICE_ID);
		iotServerConfigs.setDeviceName(AgentConstants.DEFAULT_DEVICE_NAME);
		iotServerConfigs.setControllerContext(AgentConstants.DEVICE_CONTROLLER_API_EP);
		iotServerConfigs.setHTTPS_ServerEndpoint(AgentConstants.DEFAULT_HTTPS_SERVER_EP);
		iotServerConfigs.setHTTP_ServerEndpoint(AgentConstants.DEFAULT_HTTP_SERVER_EP);
		iotServerConfigs.setApimGatewayEndpoint(AgentConstants.DEFAULT_APIM_GATEWAY_EP);
		iotServerConfigs.setMqttBrokerEndpoint(AgentConstants.DEFAULT_MQTT_BROKER_EP);
		iotServerConfigs.setXmppServerEndpoint(AgentConstants.DEFAULT_XMPP_SERVER_EP);
		iotServerConfigs.setAuthMethod(AgentConstants.DEFAULT_AUTH_METHOD);
		iotServerConfigs.setAuthToken(AgentConstants.DEFAULT_AUTH_TOKEN);
		iotServerConfigs.setRefreshToken(AgentConstants.DEFAULT_REFRESH_TOKEN);
		iotServerConfigs.setDataPushInterval(AgentConstants.DEFAULT_DATA_PUBLISH_INTERVAL);

		return iotServerConfigs;
	}


	/**
	 * This method constructs the URLs for each of the API Endpoints called by the device agent
	 * Ex: Register API, Push-Data API
	 *
	 * @throws AgentCoreOperationException if any error occurs at socket level whilst trying to
	 *                                     retrieve the deviceIP of the network-interface read
	 *                                     from the configs file
	 */
	public static void initializeHTTPEndPoints() {
		String apimEndpoint = agentManager.getAgentConfigs().getHTTP_ServerEndpoint();
		String backEndContext = agentManager.getAgentConfigs().getControllerContext();

		String deviceControllerAPIEndpoint = apimEndpoint + backEndContext;

		String registerEndpointURL =
				deviceControllerAPIEndpoint + AgentConstants.DEVICE_REGISTER_API_EP;
		agentManager.setIpRegistrationEP(registerEndpointURL);

		String pushDataEndPointURL =
				deviceControllerAPIEndpoint + AgentConstants.DEVICE_PUSH_TEMPERATURE_API_EP;
		agentManager.setPushDataAPIEP(pushDataEndPointURL);

		log.info(AgentConstants.LOG_APPENDER + "IoT Server's Device Controller API Endpoint: " +
				         deviceControllerAPIEndpoint);
		log.info(AgentConstants.LOG_APPENDER + "DeviceIP Registration EndPoint: " +
				         registerEndpointURL);
		log.info(AgentConstants.LOG_APPENDER + "Push-Data API EndPoint: " + pushDataEndPointURL);
	}


	/**
	 * This method calls the "Register-API" of the IoT Server in order to register the device's IP
	 * against its ID.
	 *
	 * @param deviceOwner the owner of the device by whose name the agent was downloaded.
	 *                    (Read from configuration file)
	 * @param deviceID    the deviceId that is auto-generated whilst downloading the agent.
	 *                    (Read from configuration file)
	 * @return the status code of the HTTP-Post call to the Register-API of the IoT-Server
	 * @throws AgentCoreOperationException if any errors occur when an HTTPConnection session is
	 *                                     created
	 */
//	public static int registerDeviceIP(String deviceOwner, String deviceID)
//			throws AgentCoreOperationException {
//		int responseCode = -1;
//
//		String networkInterface = "en0";
//		//agentManager.getAgentConfigs().getNetworkInterface();
//		String deviceIPAddress = getDeviceIP(networkInterface);
//
//		if (deviceIPAddress == null) {
//			throw new AgentCoreOperationException(
//					"An IP address could not be retrieved for the network interface - '" +
//							networkInterface + "' provided in the '" +
//							AgentConstants.AGENT_PROPERTIES_FILE_NAME + "' file.");
//		}
//
//		agentManager.setDeviceIP(deviceIPAddress);
//		log.info(AgentConstants.LOG_APPENDER + "Device IP Address: " + deviceIPAddress);
//
//		String deviceIPRegistrationEP = agentManager.getIpRegistrationEP();
//		String registerEndpointURLString =
//				deviceIPRegistrationEP + File.separator + deviceOwner + File.separator + deviceID +
//						File.separator + deviceIPAddress;
//
//		if (log.isDebugEnabled()) {
//			log.debug(AgentConstants.LOG_APPENDER + "DeviceIP Registration EndPoint: " +
//					          registerEndpointURLString);
//		}
//
//		HttpURLConnection httpConnection = getHttpConnection(registerEndpointURLString);
//
//		try {
//			httpConnection.setRequestMethod(AgentConstants.HTTP_POST);
//			httpConnection.setRequestProperty("Authorization", "Bearer " +
//					agentManager.getAgentConfigs().getAuthToken());
//			httpConnection.setDoOutput(true);
//			responseCode = httpConnection.getResponseCode();
//
//		} catch (ProtocolException exception) {
//			String errorMsg = AgentConstants.LOG_APPENDER +
//					"Protocol specific error occurred when trying to set method to " +
//					AgentConstants.HTTP_POST + " for:" + registerEndpointURLString;
//			log.error(errorMsg);
//			throw new AgentCoreOperationException(errorMsg, exception);
//
//		} catch (IOException exception) {
//			String errorMsg = AgentConstants.LOG_APPENDER +
//					"An IO error occurred whilst trying to get the response code from: " +
//					registerEndpointURLString + " for a " + AgentConstants.HTTP_POST + " method.";
//			log.error(errorMsg);
//			throw new AgentCoreOperationException(errorMsg, exception);
//		}
//
//		log.info(AgentConstants.LOG_APPENDER + "DeviceIP - " + deviceIPAddress +
//				         ", registration with IoT Server at : " +
//				         agentManager.getAgentConfigs().getHTTPS_ServerEndpoint() +
//				         " returned status " +
//				         responseCode);
//		return responseCode;
//	}


	/**
	 * This method is used to push device data to the IoT-Server via an HTTP invocation to the API.
	 * Invocation of this method calls its overloaded-method with a push-interval equal to that of
	 * the default value from "AgentConstants" class
	 *
	 * @param deviceOwner the owner of the device by whose name the agent was downloaded.
	 *                    (Read from configuration file)
	 * @param deviceID    the deviceId that is auto-generated whilst downloadng the agent.
	 *                    (Read from configuration file)
	 */
//	public static void initiateDeviceDataPush(final String deviceOwner, final String deviceID) {
//		initiateDeviceDataPush(deviceOwner, deviceID, AgentConstants.DEFAULT_DATA_PUBLISH_INTERVAL);
//	}

	/**
	 * This is an overloaded method that pushes device-data to the IoT-Server at given time
	 * intervals
	 *
	 * @param deviceOwner the owner of the device by whose name the agent was downloaded.
	 *                    (Read from configuration file)
	 * @param deviceID    the deviceId that is auto-generated whilst downloading the agent.
	 *                    (Read from configuration file)
	 * @param interval    the time interval between every successive data-push attempts.
	 *                    (initially set at startup and is read from the configuration file)
	 */
//	public static void initiateDeviceDataPush(final String deviceOwner, final String deviceID,
//	                                          int interval) {
//		final String pushDataEndPointURL = agentManager.getPushDataAPIEP();
//
//		if (log.isDebugEnabled()) {
//			log.info(AgentConstants.LOG_APPENDER + "PushData EndPoint: " + pushDataEndPointURL);
//		}
//
//		Runnable pushDataThread = new Runnable() {
//			public void run() {
//				int responseCode = -1;
//				String pushDataPayload = null;
//				HttpURLConnection httpConnection = null;
//
//				try {
//					httpConnection = getHttpConnection(pushDataEndPointURL);
//					httpConnection.setRequestMethod(AgentConstants.HTTP_POST);
//					httpConnection.setRequestProperty("Authorization", "Bearer " +
//							agentManager.getAgentConfigs().getAuthToken());
//					httpConnection.setRequestProperty("Content-Type",
//					                                  AgentConstants.APPLICATION_JSON_TYPE);
//
//					int currentTemperature = agentManager.getTemperature();
//					pushDataPayload = String.format(AgentConstants.PUSH_DATA_PAYLOAD, deviceOwner,
//					                                deviceID,
//					                                agentManager.getDeviceIP(),
//					                                currentTemperature);
//
//					if (log.isDebugEnabled()) {
//						log.debug(AgentConstants.LOG_APPENDER + "Push Data Payload is: " +
//								          pushDataPayload);
//					}
//
//					httpConnection.setDoOutput(true);
//					DataOutputStream dataOutPutWriter = new DataOutputStream(
//							httpConnection.getOutputStream());
//					dataOutPutWriter.writeBytes(pushDataPayload);
//					dataOutPutWriter.flush();
//					dataOutPutWriter.close();
//
//					responseCode = httpConnection.getResponseCode();
//					httpConnection.disconnect();
//
//				} catch (ProtocolException exception) {
//					String errorMsg = AgentConstants.LOG_APPENDER +
//							"Protocol specific error occurred when trying to set method to " +
//							AgentConstants.HTTP_POST + " for:" + pushDataEndPointURL;
//					log.error(errorMsg);
//
//				} catch (IOException exception) {
//					String errorMsg = AgentConstants.LOG_APPENDER +
//							"An IO error occurred whilst trying to get the response code from: " +
//							pushDataEndPointURL + " for a " + AgentConstants.HTTP_POST + " " +
//							"method.";
//					log.error(errorMsg);
//
//				} catch (AgentCoreOperationException exception) {
//					log.error(AgentConstants.LOG_APPENDER +
//							          "Error encountered whilst trying to create HTTP-Connection" +
//							          " to IoT-Server EP at: " + pushDataEndPointURL);
//				}
//
//				if (responseCode == HttpStatus.CONFLICT_409 ||
//						responseCode == HttpStatus.PRECONDITION_FAILED_412) {
//					log.warn(AgentConstants.LOG_APPENDER +
//							         "DeviceIP is being Re-Registered due to Push-Data failure " +
//							         "with response code: " + responseCode);
//					agentManager.registerThisDevice();
//
//				} else if (responseCode != HttpStatus.NO_CONTENT_204) {
//					if (log.isDebugEnabled()) {
//						log.error(AgentConstants.LOG_APPENDER + "Status Code: " + responseCode +
//								          " encountered whilst trying to Push-Device-Data to IoT" +
//								          " Server at: " +
//								          agentManager.getPushDataAPIEP());
//					}
//					agentManager.updateAgentStatus("Server not responding..");
//				}
//
//				if (log.isDebugEnabled()) {
//					log.debug(AgentConstants.LOG_APPENDER + "Push-Data call with payload - " +
//							          pushDataPayload + ", to IoT Server returned status " +
//							          responseCode);
//				}
//			}
//		};
//
//		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//		service.scheduleAtFixedRate(pushDataThread, 0, interval, TimeUnit.SECONDS);
//
//		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//		service.scheduleAtFixedRate(CommunicationUtils.PUSH_DATA_THREAD, 0, interval, TimeUnit.SECONDS);
//	}

	/**
	 * This method is used by the device to subscribe to any MQTT queue which is used by the WSO2
	 * IoT-Server instance.
	 * All device-specific control signals published to this queue is received via this
	 * subscription.
	 * For control signals which expects a reply in return (eg: readTemperature), the reply is
	 * published back to the same queue with an appropriate topic.
	 *
	 * @param deviceOwner        the owner of the device by whose name the agent was downloaded.
	 *                           (Read from configuration file)
	 * @param deviceID           the deviceId that is auto-generated whilst downloading the agent.
	 *                           (Read from configuration file)
	 * @param mqttBrokerEndPoint the IP Address of the MQTT Broker to which the agent is to
	 *                           subscribe. (Read from configuration file)
	 * @throws AgentCoreOperationException if any errors occur whilst trying to connect/subscribe
	 *                                     to the MQTT Queue.
	 */
//	public static void subscribeToMQTT(final String deviceOwner, final String deviceID,
//	                                   final String mqttBrokerEndPoint)
//			throws AgentCoreOperationException {
//
//		String subscribeTopic = String.format(AgentConstants.MQTT_SUBSCRIBE_TOPIC, deviceOwner,
//		                                      deviceID);
//
//		MQTTClient mqttClient = new MQTTClient(deviceOwner, deviceID,
//		                                       mqttBrokerEndPoint,
//		                                       subscribeTopic) {
//			@Override
//			protected void postMessageArrived(String topic, MqttMessage message) {
//				log.info(AgentConstants.LOG_APPENDER + "Message " + message.toString() +
//						         " was received for topic: " + topic);
//
//				String[] controlSignal = message.toString().split(":");
//				// message is in "<SIGNAL_TYPE>:<SIGNAL_MODE>" format.
//				//                           (ex: "BULB:ON", "TEMPERATURE", "HUMIDITY")
//
//				switch (controlSignal[0].toUpperCase()) {
//					case AgentConstants.BULB_CONTROL:
//						agentManager.changeBulbStatus(controlSignal[1].equals(
//								AgentConstants.CONTROL_ON) ? true : false);
//						log.info(AgentConstants.LOG_APPENDER + "Bulb was switched to state: '" +
//								         controlSignal[1] + "'");
//						break;
//
//					case AgentConstants.TEMPERATURE_CONTROL:
//						int currentTemperature = agentManager.getTemperature();
//
//						String replyTemperature =
//								"Current temperature was read as: '" + currentTemperature + "C'";
//						log.info(AgentConstants.LOG_APPENDER + replyTemperature);
//
//						String tempPublishTopic = String.format(
//								AgentConstants.MQTT_PUBLISH_TOPIC, deviceOwner, deviceID);
//						publishPayloadToMQTT(tempPublishTopic,
//						                     (AgentConstants.TEMPERATURE_CONTROL + ":" +
//								                     currentTemperature));
//						break;
//
//					case AgentConstants.HUMIDITY_CONTROL:
//						int currentHumidity = agentManager.getHumidity();
//
//						String replyHumidity =
//								"Current humidity was read as: '" + currentHumidity + "%'";
//						log.info(AgentConstants.LOG_APPENDER + replyHumidity);
//
//						String humidPublishTopic = String.format(
//								AgentConstants.MQTT_PUBLISH_TOPIC, deviceOwner, deviceID);
//						publishPayloadToMQTT(humidPublishTopic,
//						                     (AgentConstants.HUMIDITY_CONTROL + ":" +
//								                     currentHumidity));
//						break;
//
//					default:
//						log.warn(
//								"'" + controlSignal[0] + "' is invalid and not-supported for " +
//										"this device-type");
//						break;
//				}
//			}
//		};
//
//		agentManager.setAgentMQTTClient(mqttClient);
//		agentManager.getAgentMQTTClient().connectAndSubscribe();
//	}


	/**
	 * This method is used to publish reply-messages for the control signals received.
	 * Invocation of this method calls its overloaded-method with a QoS equal to that of the
	 * default value from "AgentConstants" class.
	 *
	 * @param publishTopic the topic to which the reply message is to be published.
	 * @param payLoad      the reply-message (payload) of the MQTT publish action.
	 */
//	private static void publishPayloadToMQTT(String publishTopic, String payLoad) {
//		publishPayloadToMQTT(publishTopic, payLoad, AgentConstants
//				.DEFAULT_MQTT_QUALITY_OF_SERVICE);
//	}

	/**
	 * This is an overloaded method that publishes MQTT reply-messages for control signals
	 * received form the IoT-Server.
	 *
	 * @param publishTopic the topic to which the reply message is to be published
	 * @param payLoad      the reply-message (payload) of the MQTT publish action.
	 * @param qos          the Quality-of-Service of the current publish action.
	 *                     Could be 0(At-most once), 1(At-least once) or 2(Exactly once)
	 */
//	private static void publishPayloadToMQTT(String publishTopic, String payLoad, int qos) {

//		MQTTClient agentMQTTClient = agentManager.getAgentMQTTClient();
//
//		try {
//			agentMQTTClient.getClient().publish(publishTopic, payLoad.getBytes(
//					StandardCharsets.UTF_8), qos, true);
//			if (log.isDebugEnabled()) {
//				log.debug("Message: " + payLoad + " to MQTT topic [" + publishTopic +
//						          "] published successfully");
//			}
//		} catch (MqttException ex) {
//			String errorMsg =
//					"MQTT Client Error" + "\n\tReason:  " + ex.getReasonCode() + "\n\tMessage: " +
//							ex.getMessage() + "\n\tLocalMsg: " + ex.getLocalizedMessage() +
//							"\n\tCause: " + ex.getCause() + "\n\tException: " + ex;
//			log.info(AgentConstants.LOG_APPENDER + errorMsg);
//		}
//	}


	/**
	 * This method is used by the device to connect to any XMPP Server which is used by the WSO2
	 * IoT-Server instance. All device-specific control signals sent to the device's XMPP Account
	 * is received via this connection. For control signals which expects a reply in return
	 * (eg: readTemperature), the reply message is sent back to the WSO2-IoT Server's XMPP account.
	 *
	 * @param username           the login-username of the xmpp account the device is attached to
	 * @param password           the account password of the device's xmpp account
	 * @param resource           the resource, specific to the xmpp account to which the login is
	 *                           made to
	 * @param xmppServerEndPoint the IP/Domain of the XMPP Server to connect to
	 * @throws AgentCoreOperationException when the XMPP-Server Endpoint information given in the
	 *                                     'deviceConfig.properties' file is invalid or when the
	 *                                     connection/login to the XMPP Server fails due to
	 *                                     inappropriate credentials.
	 */
//	public static void connectToXMPPServer(
//			final String username, final String password, final String resource,
//			String xmppServerEndPoint)
//			throws AgentCoreOperationException {
//
//		Map<String, String> ipPortMap = getHostAndPort(xmppServerEndPoint);
//		String server = ipPortMap.get("Host");
//		int port = Integer.parseInt(ipPortMap.get("Port"));
//
//		final String xmppDeviceJID = username + "@" + server;
//		final String xmppAdminJID = AgentConstants.XMPP_ADMIN_ACCOUNT_UNAME + "@" + server;
//		agentManager.setXmppAdminJID(xmppAdminJID);
//
//		XMPPClient xmppClient = new XMPPClient(server, port) {
//			@Override
//			protected void processXMPPMessage(Message xmppMessage) {
//
//			}
//		};
//
//		agentManager.setAgentXMPPClient(xmppClient);
//		agentManager.getAgentXMPPClient().connectAndLogin(username, password, resource);
////		agentManager.getAgentXMPPClient().setMessageFilterAndListener(xmppAdminJID);
//		agentManager.getAgentXMPPClient().setMessageFilterAndListener(xmppAdminJID, xmppDeviceJID,
//		                                                              true);
//	}

}

