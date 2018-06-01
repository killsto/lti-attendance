--Note: This file MUST BE 1 line per statement, if your statement is long, it CANNOT (hard) wrap to a new line
insert into LTI_KEY (key_id, key_key, secret, consumer_profile) values (1, '5396b5be4a75b40e8131', '9564a64cea7ab33a45ac', null);

insert into CONFIG_ITEM (config_id, lti_application, config_key, config_value) values (1, 'COMMON', 'canvas_url', 'https://k-state.test.instructure.com');
insert into CONFIG_ITEM (config_id, lti_application, config_key, config_value) values (2, 'COMMON', 'canvas_url_2', 'https://k-state.test.instructure.com');

insert into CONFIG_ITEM (config_id, lti_application, config_key, config_value) values (3, 'Attendance', 'launch_url', 'https://localhost:8443/launch');
insert into CONFIG_ITEM (config_id, lti_application, config_key, config_value) values (4, 'Attendance', 'oauth_client_id', '17260000000000055');
insert into CONFIG_ITEM (config_id, lti_application, config_key, config_value) values (5, 'Attendance', 'oauth_client_secret','HV3cbjepezScPcuM4OGKuV4P3JXdAtHKCYGIGuLSLWXal9LxEdWD4endR6XXrNvf');