package syk.sample.gcs.main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syk.common.MavJsonListener;
import syk.common.MavJsonMessage;
import syk.gcs.cameraview.CameraView;
import syk.gcs.cameraview.ImageListener;
import syk.gcs.dialog.AlertDialog;
import syk.gcs.hudview.Hud;
import syk.gcs.mapview.FlightMap;
import syk.gcs.mapview.FlightMapController;
import syk.gcs.mapview.MapListener;
import syk.gcs.messageview.MessageView;
import syk.sample.gcs.main.Combine01.CombineDialog01;
import syk.sample.gcs.main.Service01.ServiceDialog01;
import syk.sample.gcs.main.Service02.ServiceDialog02;
import syk.sample.gcs.main.Service03.ServiceDialog03;
import syk.sample.gcs.main.Service04.ServiceDialog04;
import syk.sample.gcs.main.Service05.ServiceDialog05;
import syk.sample.gcs.network.Drone;
import syk.sample.gcs.network.NetworkConfig;


import java.net.URL;
import java.util.ResourceBundle;


public class GcsMainController implements Initializable {

	//---------------------------------------------------------------------------------
	private static Logger logger = LoggerFactory.getLogger(GcsMainController.class);
	//---------------------------------------------------------------------------------
	@FXML public Button btnConnectConfig;
	@FXML public Button btnConnect;
	@FXML public Button btnArm;
	@FXML public TextField txtTakeoffHeight;
	@FXML public Button btnTakeoff;
	@FXML public Button btnLand;
	@FXML public Button btnRtl;
	@FXML public Button btnManual;
	@FXML public CheckBox chkManualMove;
	@FXML public CheckBox chkManualAlt;
	@FXML public TextField txtManualAlt;
	@FXML public Button btnMissionMake;
	@FXML public Button btnMissionClear;
	@FXML public Button btnMissionUpload;
	@FXML public Button btnMissionDownload;
	@FXML public Button btnMissionStart;
	@FXML public Button btnMissionStop;
	@FXML public Button btnGetMissionFromFile;
	@FXML public Button btnSaveMissionToFile;
	@FXML public Button btnFenceMake;
	@FXML public Button btnFenceClear;
	@FXML public Button btnFenceUpload;
	@FXML public Button btnFenceDownload;
	@FXML public Button btnFenceEnable;
	@FXML public Button btnFenceDisable;
	@FXML public Button btnMessageView;
	@FXML public Button btnCameraView;
	@FXML public Button btnNorth;
	@FXML public Button btnSouth;
	@FXML public Button btnEast;
	@FXML public Button btnWest;
	// ------------------------------
	@FXML public Button Service01Btn;
	@FXML public Button Service02Btn;
	@FXML public Button Service03Btn;
	@FXML public Button Service04Btn;
	@FXML public Button HtmlBtn;
	@FXML public Button RootSetBtn;

	public Drone drone;

	private boolean FCMqttClientTrigger_GPS = false;
	private boolean FCMqttClientTrigger_Mission = false;

	// 미션을 받았다면 발동하여 그때부터 드론 GPS 좌표를 전송한다.
	private boolean WebMissionInTrigger = false;

	// Service04Dialog 가 작동되었는지 확인하기 위함. 0 : 미작. 1 : 작동완료
	private int Service04Activated = 0;

	// 안드로이드와 Web 에서 전송될 토픽 /gcs/main 에 대한 subscribe 되는 클라이언트 이다.
	private GcsMainMqtt gcsMainMqtt;

	private double destinationLat;
	private double destinationLng;
	// 미션 마커 이벤트용
	private int MissionDestinationMarkerTrigger = 0;

	private int GpsSendThread = 0;


	//---------------------------------------------------------------------------------
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnConnectConfig.setOnAction(btnConnectConfigEventHandler);
		btnConnect.setOnAction(btnConnectEventHandler);
		btnArm.setOnAction(btnArmEventHandler);
		btnTakeoff.setOnAction(btnTakeoffEventHandler); btnTakeoff.setDisable(true); 
		btnLand.setOnAction(btnLandEventHandler); btnLand.setDisable(true);
		btnRtl.setOnAction(btnRtlEventHandler);	btnRtl.setDisable(true);
		btnManual.setOnAction(btnManualEventHandler); btnManual.setDisable(true);	
		btnMissionMake.setOnAction(btnMissionMakeEventHandler); btnMissionMake.setDisable(true);
		btnMissionClear.setOnAction(btnMissionClearEventHandler); btnMissionClear.setDisable(true);
		btnMissionUpload.setOnAction(btnMissionUploadEventHandler); btnMissionUpload.setDisable(true);
		btnMissionDownload.setOnAction(btnMissionDownloadEventHandler); btnMissionDownload.setDisable(true);
		btnMissionStart.setOnAction(btnMissionStartEventHandler); btnMissionStart.setDisable(true);
		btnMissionStop.setOnAction(btnMissionStopEventHandler); btnMissionStop.setDisable(true);
		btnGetMissionFromFile.setOnAction(btnGetMissionFromFileEventHandler); btnGetMissionFromFile.setDisable(true);
		btnSaveMissionToFile.setOnAction(btnSaveMissionToFileEventHandler); btnSaveMissionToFile.setDisable(true);
		btnFenceMake.setOnAction(btnFenceMakeEventHandler); btnFenceMake.setDisable(true);
		btnFenceClear.setOnAction(btnFenceClearEventHandler); btnFenceClear.setDisable(true);
		btnFenceUpload.setOnAction(btnFenceUploadEventHandler); btnFenceUpload.setDisable(true);
		btnFenceDownload.setOnAction(btnFenceDownloadEventHandler); btnFenceDownload.setDisable(true);
		btnFenceEnable.setOnAction(btnFenceEnableEventHandler); btnFenceEnable.setDisable(true);
		btnFenceDisable.setOnAction(btnFenceDisableEventHandler); btnFenceDisable.setDisable(true);
		btnMessageView.setOnAction(btnMessageViewEventHandler);
		btnCameraView.setOnAction(btnCameraViewEventHandler);
		btnNorth.setOnAction(btnNorthEventHandler);
		btnSouth.setOnAction(btnSouthEventHandler);
		btnEast.setOnAction(btnEastEventHandler);
		btnWest.setOnAction(btnWestEventHandler);

		//---------------
		Service01Btn.setOnAction(Service01BtnEventHandler);
		Service02Btn.setOnAction(Service02BtnEventHandler);
		Service03Btn.setOnAction(Service03BtnEventHandler);
		Service04Btn.setOnAction(Service04BtnEventHandler);
		RootSetBtn.setOnAction(RootSetBtn_Handler);
		HtmlBtn.setOnAction(HtmlBtnHander);


		drone = new Drone();

		// 드론 hub 삽입
		initHud();

		// 메시지 뷰 삽입
		initMessageView();

		// 카메라 삽입
		initCameraView();

		// GoogleMap 삽입
		initFlightMap();

		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HEARTBEAT,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonObject) {
						Platform.runLater(()->{
							btnConnect.setText("연결끊기");
							if(jsonObject.getBoolean("arm")) {
								btnArm.setText("시동끄기");
								btnTakeoff.setDisable(false);
								btnLand.setDisable(false);
								btnRtl.setDisable(false);
								btnManual.setDisable(false);
							} else {
								btnArm.setText("시동걸기");
								btnTakeoff.setDisable(true);
								btnLand.setDisable(true);
								btnRtl.setDisable(true);
								btnManual.setDisable(true);
								if(!drone.flightController.mode.equals(MavJsonMessage.MAVJSON_MODE_STABILIZE)) {
									drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_STABILIZE);
								}
							}
						});
					}
				}
		);
	}
	//---------------------------------------------------------------------------------
	@FXML public StackPane hudPane;
	public Hud hud;
	public void initHud() {
		hud = new Hud();
		hudPane.getChildren().add(hud.ui);
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HEARTBEAT,
        		new MavJsonListener() {
		            @Override
		            public void receive(JSONObject jsonMessage) {
		                hud.controller.setMode(jsonMessage.getString("mode"));
		                hud.controller.setArm(jsonMessage.getBoolean("arm"));
		            }
		        });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_GLOBAL_POSITION_INT,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	hud.controller.setAlt(jsonMessage.getDouble("alt"));
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_ATTITUDE,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
						double yaw = jsonMessage.getDouble("yaw");
						if(yaw < 0) {
							yaw += 360;
						}
                    	hud.controller.setRollPichYaw(
								jsonMessage.getDouble("roll"),
								jsonMessage.getDouble("pitch"),
								yaw
						);
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_VFR_HUD,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	hud.controller.setSpeed(
                    			jsonMessage.getDouble("airSpeed"),
								jsonMessage.getDouble("groundSpeed"));
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_SYS_STATUS,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	hud.controller.setBattery(
								jsonMessage.getDouble("voltageBattery"),
								jsonMessage.getDouble("currentBattery"),
								jsonMessage.getInt("batteryRemaining")
						);
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_GPS_RAW_INT,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	hud.controller.setGpsFixed(jsonMessage.getString("fix_type"));
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_STATUSTEXT,
                new MavJsonListener() {
                    private String text;
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	hud.controller.setStatusText(jsonMessage.getString("text"));
                    }
                });

		hud.controller.btnCamera.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(hud.controller.isVideoOn == false) {
					hud.controller.videoOn();
					drone.camera0.mqttListenerSet(new ImageListener() {
						@Override
						public void receive(byte[] image) {
						    hud.controller.videoImage(image);
						}
					});
				} else {
				    hud.controller.videoOff();
				    drone.camera0.mqttListenerSet(null);
				}
			}
		});
	}
	//---------------------------------------------------------------------------------
	@FXML public StackPane messageCamPane;
	public MessageView messageView;
	public void initMessageView() {
		messageView = new MessageView();
		messageCamPane.getChildren().add(messageView.ui);
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_RECEIVE_MESSAGE_ALL,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						messageView.controller.addReceiveMessage(jsonMessage);
					}
				}
		);
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_SEND_MESSAGE_ALL,
				new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						messageView.controller.addSendMessage(jsonMessage);
					}
				}
		);
	}
	//---------------------------------------------------------------------------------
	public CameraView cameraView;
	public void initCameraView() {
		cameraView = new CameraView();
		messageCamPane.getChildren().add(cameraView.ui);
		cameraView.ui.setVisible(false);
		drone.camera1.mqttListenerSet(new ImageListener() {
			@Override
			public void receive(byte[] image) {
				cameraView.controller.videoImage(image);
			}
		});
	}
	//---------------------------------------------------------------------------------
	@FXML public BorderPane centerBorderPane;
	// 이하의 것을 얻어야 googleMap에 직접적으로 접근이 가능하다.
	public FlightMap flightMap;
	public void initFlightMap() {
		flightMap = new FlightMap();
		flightMap.setApiKey("AIzaSyBR_keJURT-bAce2vHKIWKNQTC-GqJWRMI");
		centerBorderPane.setCenter(flightMap.ui);
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HEARTBEAT,
        		new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						String mode = jsonMessage.getString("mode");
						flightMap.controller.setMode(mode);
						
						if(drone.flightController.homeLat == 0.0) {
                        	drone.flightController.sendGetHomePosition();
                        }
					}
				});
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_GLOBAL_POSITION_INT,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	flightMap.controller.setCurrLocation(
                    			jsonMessage.getDouble("currLat"), 
                    			jsonMessage.getDouble("currLng"), 
                    			jsonMessage.getDouble("heading"));
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_HOME_POSITION,
        		new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.setHomePosition(
								jsonMessage.getDouble("homeLat"), 
								jsonMessage.getDouble("homeLng"));
						btnMissionMake.setDisable(false);
						btnMissionClear.setDisable(false);
						btnMissionUpload.setDisable(false);
						btnMissionDownload.setDisable(false);
						btnMissionStart.setDisable(false);
						btnMissionStop.setDisable(false);
						btnGetMissionFromFile.setDisable(false);
						btnSaveMissionToFile.setDisable(false);
						btnFenceMake.setDisable(false);
						btnFenceClear.setDisable(false);
						btnFenceUpload.setDisable(false);
						btnFenceDownload.setDisable(false);
						btnFenceEnable.setDisable(false);
						btnFenceDisable.setDisable(false);						
					}
				});
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_MISSION_ACK,
        		new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.showInfoLabel("미션 업로드 성공");
					}
				});		
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_MISSION_ITEMS,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	flightMap.controller.setMissionItems(jsonMessage.getJSONArray("items"));
                    	flightMap.controller.showInfoLabel("미션 다운로드 성공");
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_MISSION_CURRENT,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	flightMap.controller.setMissionCurrent(jsonMessage.getInt("seq"));
                    }
                });
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_FENCE_ACK,
        		new MavJsonListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						flightMap.controller.showInfoLabel("펜스 업로드 성공");
					}
				});	
		
		drone.flightController.addMavJsonListener(
				MavJsonMessage.MAVJSON_MSG_ID_FENCE_POINTS,
                new MavJsonListener() {
                    @Override
                    public void receive(JSONObject jsonMessage) {
                    	flightMap.controller.fenceMapSync(jsonMessage.getJSONArray("points"));
                    }
                });
	}
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMessageViewEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if(messageView != null) {
				messageView.ui.setVisible(true);
			}
			if(cameraView != null) {
				cameraView.ui.setVisible(false);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnCameraViewEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if(messageView != null) {
				messageView.ui.setVisible(false);
			}
			if(cameraView != null) {
				cameraView.ui.setVisible(true);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnConnectConfigEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			try {
				Stage dialog = new Stage();
				dialog.setTitle("Network Configuration");
				dialog.initModality(Modality.APPLICATION_MODAL);
				Scene scene = new Scene(NetworkConfig.getInstance().ui);
				scene.getStylesheets().add(GcsMain.class.getResource("style_dark_dialog.css").toExternalForm());
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	//---------------------------------------------------------------------------------

	private FCMqttClient fcMqttClient = null;
	public EventHandler<ActionEvent> btnConnectEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if(btnConnect.getText().equals("연결하기")) {
				drone.connect();
				// /gcs/main 		subscribe
				gcsMainMqtt = new GcsMainMqtt();
				System.out.println("GcsMainMqtt Created");
				// /drone/fc/pub 	subscribe
				fcMqttClient = new FCMqttClient();
				System.out.println("jangMqttClient Created Success");

			} else {
				drone.disconnect();
				btnConnect.setText("연결하기");
				btnArm.setText("시동걸기");
				//jangMqttClient.disconnectThread();
			}

		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnArmEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			if (btnArm.getText().equals("시동걸기")) {
				drone.flightController.sendArm(true);
			} else {
				drone.flightController.sendArm(false);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnTakeoffEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			float alt = Float.parseFloat(txtTakeoffHeight.getText());
			drone.flightController.sendTakeoff(alt);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnLandEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//drone.flightController.sendSetMode("LAND");
			drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_LAND);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnRtlEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//drone.flightController.sendSetMode("RTL");
			drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_RTL);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnManualEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			boolean isMove = chkManualMove.isSelected();
			boolean isAlt = chkManualAlt.isSelected();
			double manualAlt = Double.parseDouble(txtManualAlt.getText());

			if(isMove==false && isAlt==true) {
				drone.flightController.sendSetPositionTargetGlobalInt(
						drone.flightController.currLat,
						drone.flightController.currLng,
						manualAlt
				);
				return;
			}
			
			if(isMove == true) {
				flightMap.controller.mapListenerAdd("manualMove", new MapListener() {
					@Override
					public void receive(JSONObject jsonMessage) {
						drone.flightController.sendSetPositionTargetGlobalInt(
								jsonMessage.getDouble("targetLat"),
								jsonMessage.getDouble("targetLng"),
								jsonMessage.getDouble("targetAlt")
						);
					}
				});
				
				if(isAlt) {
					flightMap.controller.manualMake(manualAlt);
				} else {
					flightMap.controller.manualMake(drone.flightController.alt);
				}
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionMakeEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.missionMake();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionClearEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.missionClear();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionUploadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			JSONArray jsonArray = flightMap.controller.getMissionItems();
			if(jsonArray.length() < 2) {
				AlertDialog.showOkButton("알림", "미션 아이템 수가 부족합니다.");
			} else {
				drone.flightController.sendMissionUpload(jsonArray);
			}
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionDownloadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendMissionDownload();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionStartEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendMissionStart();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnMissionStopEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			//drone.flightController.sendSetMode("GUIDED");
			drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_GUIDED);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnGetMissionFromFileEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.readMissionFromFile();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnSaveMissionToFileEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.writeMissionToFile();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceMakeEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.fenceMake();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceClearEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.fenceClear();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceUploadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			flightMap.controller.mapListenerAdd("fencePoints", new MapListener() {
				@Override
				public void receive(JSONObject jsonMessage) {
					JSONArray jsonArray = jsonMessage.getJSONArray("points");
					if(jsonArray.length() < 4) {
						AlertDialog.showOkButton("알림", "펜스 포인트 수가 부족합니다.");
					} else {
						drone.flightController.sendFenceUpload(jsonArray);
					}
				}
			});
			
			flightMap.controller.getFencePoints();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceDownloadEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFenceDownload();
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceEnableEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFenceEnable(true);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnFenceDisableEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFenceEnable(false);
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnNorthEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(1, 0); //m/s
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnSouthEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(-1, 0); //m/s
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnEastEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(0, 1); //m/s
		}
	};
	//---------------------------------------------------------------------------------
	public EventHandler<ActionEvent> btnWestEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			drone.flightController.sendFindControl(0, -1); //m/s
		}
	};

	public EventHandler<ActionEvent> Service01BtnEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("Service01Btn Clicked");
			ServiceDialog01 serviceDialog01 = new ServiceDialog01();
			serviceDialog01.show();
		}
	};

	public EventHandler<ActionEvent> Service02BtnEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("Service02Btn Clicked");
			ServiceDialog02 serviceDialog02 = new ServiceDialog02();
			serviceDialog02.show();
			//ServiceDialog02 serviceDialog02 = new ServiceDialog02();
			serviceDialog02.show();
		}
	};

	public EventHandler<ActionEvent> Service03BtnEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("Service03Btn Clicked");
			ServiceDialog03 serviceDialog03 = new ServiceDialog03();
			serviceDialog03.show();
			//ServiceDialog02 serviceDialog02 = new ServiceDialog02();
			//GcsMain.instance.controller.flightMap.controller.showInfoLabel("목적지에 도착했습니다.");
		}
	};

	public EventHandler<ActionEvent> Service04BtnEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("Service04Btn Clicked");
			ServiceDialog04 serviceDialog04 = new ServiceDialog04();
			serviceDialog04.show();
			//ServiceDialog02 serviceDialog02 = new ServiceDialog02();
			//AlertDialog.showOkButton("알림", "목적지에 도달했습니다.");
		}
	};

	public EventHandler<ActionEvent> HtmlBtnHander = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("HtmlBtn Clicked");
			ServiceDialog05 serviceDialog05 = new ServiceDialog05();
			serviceDialog05.show();
		}
	};

	public EventHandler<ActionEvent> RootSetBtn_Handler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("RootSetBtn Activate");
			rootSetMethod();
		}
	};


	// GCS 전체에 들어오는 정보를 받는 client
	public class FCMqttClient{
		private MqttClient client;

		private String gpsLat;
		private String gpsLng;

		public FCMqttClient(){
			while(true){
				try {
					client = new MqttClient("tcp://106.253.56.124:1881", MqttClient.generateClientId(), null);
					MqttConnectOptions options = new MqttConnectOptions();
					client.connect();
					break;
				}
				catch (MqttException e){
					System.out.println(e.getMessage());
				}
			}

			System.out.println("FCMqttClient MQTT Connected");

			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable throwable) {
					System.out.println("FCMqttClient Connection Lost");
				}

				@Override
				public void messageArrived(String s, MqttMessage mqttMessage){
					/*
                    System.out.println("Message Received from Raspi total Log");
                    String getMsg = new String(mqttMessage.getPayload());
                    System.out.println("Received : " + getMsg);
					*/
					String msg = mqttMessage.toString();
					JSONObject jsonObject = new JSONObject(msg);
					String trigger = "HEARTBEAT";
					if(jsonObject.get("msgid").equals(trigger)){
						FCMqttClientTrigger_GPS = true;
						FCMqttClientTrigger_Mission = true;
					}
					// Web에서 mission start에 해당하는 mqtt를 수신하여 true로 바꾸었다면
					if(WebMissionInTrigger){
						// 마커 생성
						if(MissionDestinationMarkerTrigger == 0){
							MissionDestinationMarkerTrigger = 1;
						}
						if(MissionDestinationMarkerTrigger == 1){
							GcsMain.instance.controller.flightMap.controller.requestMark(destinationLat, destinationLng);
							MissionDestinationMarkerTrigger = 2;
						}
						// 현재 미션 상태에 따른 MissionCurrent를 받아 목적지에 도착하면
						// 새로운 형태의 Dialog 가 show 될 수 있도록
						if(jsonObject.get("msgid").equals("MISSION_CURRENT")){
							int missionSize =  GcsMain.instance.controller.flightMap.controller.getMissionItems().length();
							System.out.println("MissionSize : " + missionSize);
							int missionCurrentSeq = (int) jsonObject.get("seq");
							System.out.println("MissionCurrent : " + missionCurrentSeq);
							if(missionCurrentSeq == missionSize / 2 + 1){
								if(Service04Activated == 0){
									/*
									try {
										client.close();
									} catch (MqttException e) {
										System.out.println("FCMqttClient close");
										e.printStackTrace();
									}
									 */

									// FXML파일은 JavaFxApplication 이라는 Thread에서만
									// 변형 및 생성이 가능하기 때문에 만약 EventHandler<ActionEvent>에서
									// 등록 및 실행하는 것이 아니라면 불가능하다.
									// 그렇기 때문에 임의의 위치에서 FXML을 다른 곳에서 생성 및 변형한다면
									// Platform.runLater(new Runnable()){
									// 		@Override
									//		public void run(){
									//		}
									// }
									// 위와 같이 run 을 이용하여 강제로 JavaFxApplicationThread 에 등록하여야 한다.
									// 즉 Thread에 input하는 것과 같다.
									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											System.out.println("Service04 Activated");
											ServiceDialog04 serviceDialog04 = new ServiceDialog04();
											serviceDialog04.show();
											System.out.println("Service04Activated 0 to 1");
											Service04Activated = 1;
										}
									});
									// drone 상태를 GUIDED로 변경하기
									drone.flightController.sendSetMode(MavJsonMessage.MAVJSON_MODE_GUIDED);
								}
							}
						}
						// Web과 Android에 드론의 GPS 정보를 보내주기 위함
						else if(jsonObject.get("msgid").equals("GLOBAL_POSITION_INT")){
							System.out.println("--------------");
							gpsLat = String.valueOf(jsonObject.get("currLat"));
							gpsLng = String.valueOf(jsonObject.get("currLng"));
							JSONObject object = new JSONObject();
							object.put("data", "gps");
							object.put("lat", gpsLat);
							object.put("lng", gpsLng);
							//String latitube = String.valueOf(jsonObject.get("currLat"));
							//String longitube = String.valueOf(jsonObject.get("currLng"));

							try {
								Thread thread = new Thread(){
									@Override
									public void run(){
										try {
											System.out.println("GPS Publish Try");
											client.publish("/web/drone/sub", object.toString().getBytes(), 0,false);
											System.out.println("GPS Publish Done");
										} catch (MqttException e) {
											System.out.println("GPS Thread Error");
											e.printStackTrace();
										}
									}
								};
								thread.start();
								GpsSendThread = 1;
							}
							catch (Exception e){
								System.out.println("GPS Fail");
								System.out.println(e.getMessage());
							}
							FCMqttClientTrigger_GPS = false;
						}
					}
				}
				@Override
				public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

				}
			});
			try {
				client.subscribe("/drone/fc/pub");
				System.out.println("FCMqttClient subscribe done");
			} catch (MqttException e) {
				System.out.println("FCMqttClient subscribe fail");
				e.printStackTrace();
			}
		}
	}

	public class GcsMainMqtt{

		private MqttClient client;

		public GcsMainMqtt(){
			while(true){
				try {
					client = new MqttClient("tcp://106.253.56.124:1881", MqttClient.generateClientId(), null);
					MqttConnectOptions options = new MqttConnectOptions();
					client.connect();
					System.out.println("GcsMainMqtt client Connect Done");
					break;
				}
				catch (MqttException e){
					System.out.println("GcsMainMqtt client Connect Error");
					System.out.println(e.getMessage());
				}
			}
			make_sub();
		}

		public void make_sub(){
			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable throwable) {
					System.out.println("GcsMainMqtt Connection Lost");
				}

				@Override
				public void messageArrived(String s, MqttMessage mqttMessage){

					String strmsg = new String(mqttMessage.getPayload());
					JSONObject jsonObject = new JSONObject(strmsg);
					System.out.println("Message Arrived. MSGID : " + jsonObject.get("msgid"));
					if(jsonObject.get("msgid").equals("missionStart")){
						// msgid : missionStart 를 전달받으면 그 내부에
						// 목적지 Lat, Lng 좌표를 전달받아 미션을 생성할 때, 사용할 수 있도록 한다.
						destinationLat = (double) jsonObject.get("lat");
						destinationLng = (double) jsonObject.get("lng");
						// missionStart 메세지가 web 으로 부터 전달받으면 Combine01Dialog01 생성
						if(!WebMissionInTrigger){
							// combine01dialog 가 실행되지 않았을 때 뜨도록
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									System.out.println("CombineDialog01 Activated");
									CombineDialog01 combineDialog01 = new CombineDialog01();
									combineDialog01.show();
								}
							});
							// 미션 시작에 따른 트리거 true로 변경
						}
						WebMissionInTrigger = true;
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

				}
			});
			try {
				client.subscribe("/gcs/main");
				System.out.println("GCSMainMqtt client Subscribe Success");
			} catch (MqttException e) {
				System.out.println("GCSMainMqtt client Subscribe Fail");
				e.printStackTrace();
			}
		}
	}

	public void rootSetMethod(){
		// 현재 미션 아이템을 가져와 여기에 마지막 부분에 미션 아이템을 추가한다.
		JSONArray array = GcsMain.instance.controller.flightMap.controller.getMissionItems();
		int arrSize = array.length();
		// 목적지 seq 저장
		JSONObject destiObject = new JSONObject();
		destiObject.put("seq", arrSize);
		destiObject.put("command",16);
		destiObject.put("x", destinationLat);
		destiObject.put("y", destinationLng);
		destiObject.put("z", 10);
		destiObject.put("param1", 0);
		destiObject.put("param2", 0);
		destiObject.put("param3", 0);
		destiObject.put("param4", 0);
		System.out.println("destiObject : " + destiObject.toString());
		array.put(destiObject);
		System.out.println("---------");

		// 목적지 도착 후 delay를 주기위함
		JSONObject delayObject = new JSONObject();
		delayObject.put("seq", arrSize + 1);
		delayObject.put("command",93);
		delayObject.put("x",destinationLat);
		delayObject.put("y",destinationLat);
		delayObject.put("z",10);
		delayObject.put("param1", 10); // 딜레이 되는 시간
		delayObject.put("param2", 0);
		delayObject.put("param3", 0);
		delayObject.put("param4", 0);
		System.out.println("delayObject : " + delayObject);
		array.put(delayObject);
		System.out.println("--------------");

		// root 목적지 제외하여 변경토록 함
		JSONArray getAdd = GcsMain.instance.controller.flightMap.controller.getMissionItems();
		System.out.println("Will add");
		int idx = array.length(); // 추가되기 전의 인덱스를 가져와 seq 변경
		// int i = getAdd.length() - 1 추가되기 전의 온전한 리스트를 가져와(경로만있는)
		// 이를 적용한다. 즉 경로만 지정된 것에서 마지막 부분을 제외하고 seq를 다시 set하고
		// array에 추가한다.
		for(int i = getAdd.length() - 1 ; i > 0 ; i--){
			JSONObject object = (JSONObject) getAdd.get(i);
			object.put("seq", idx); // 변경된 arra
			System.out.println(object.toString());
			idx++;
			array.put(object);
		}

		// 마지막 root를 지우고 이를 RTL로 변경
		JSONObject RTL = new JSONObject();
		RTL.put("seq", array.length());
		RTL.put("x", destinationLat);
		RTL.put("y", destinationLng);
		RTL.put("z", 0);
		RTL.put("command", 20);
		RTL.put("param1", 0);
		RTL.put("param2", 0);
		RTL.put("param3", 0);
		RTL.put("param4", 0);
		System.out.println("Last RTL : " + RTL.toString());
		array.put(RTL);

		// 이륙 추가


		GcsMain.instance.controller.flightMap.controller.missionClear();
		// 설계한 미션들 삭제
		//GcsMain.instance.controller.flightMap.controller.missionMake();
		GcsMain.instance.controller.flightMap.controller.setMissionItems_Customize(array);
	}
}
