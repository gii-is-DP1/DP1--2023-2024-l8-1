-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);

/*CONTRASEÑA DE PLAYER=0wn3r*/
INSERT INTO authorities(id,authority) VALUES (5,'PLAYER');
INSERT INTO appusers(id,username,password,authority) VALUES (24,'player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (25,'player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (26,'player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (27,'manubrioh03','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (28,'loza3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (29,'urbanoblon','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (30,'ejemplo1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (31,'ejemplo2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (32,'ejemplo3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (33,'ejemplo4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (34,'ejemplo5','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (35,'ejemplo6','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (36,'ejemplo7','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (37,'ejemplo8','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (38,'ejemplo9','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (39,'ejemplo10','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (40,'ejemplo11','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (41,'ejemplo12','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);
INSERT INTO appusers(id,username,password,authority) VALUES (42,'ejemplo13','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',5);


INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (1,2,0,false,24,'Play','Yer');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (2,0,5,false,25,'Play','Yer');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (3,0,0,false,26,'Play','Yer');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (4,0,12,false,27,'Manuel','Serrano');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (5,2,5,false,28,'Raúl','Lozano');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (6,0,0,false,29,'Urbano','Blanes');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (7,0,0,false,30,'ejemplo','1');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (8,0,0,false,31,'ejemplo','2');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (9,0,0,false,32,'ejemplo','3');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (10,0,0,false,33,'ejemplo','4');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (11,0,0,false,34,'ejemplo','5');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (12,0,0,false,35,'ejemplo','6');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (13,0,0,false,36,'ejemplo','7');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (14,0,0,false,37,'ejemplo','8');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (15,0,0,false,38,'ejemplo','9');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (16,0,0,false,39,'ejemplo','10');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (17,0,0,false,40,'ejemplo','11');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (18,0,0,false,41,'ejemplo','12');
INSERT INTO players(id,rol,score,start_player,user_id,first_name,last_name) VALUES (19,0,0,false,42,'ejemplo','13');

/*
INSERT INTO games(id, host_id, name, publica, state) VALUES (1, 1, 'prueba', true, 'LOBBY');
INSERT INTO games(id, host_id, name, publica,  state) VALUES (2, 2, 'prueba2', true, 'LOBBY');
INSERT INTO games(id, host_id, name, publica, state) VALUES (3, 3, 'prueba3', true, 'OVER');
INSERT INTO games(id, host_id, name, publica, state) VALUES (4, 1, 'prueba4', false,'LOBBY');
INSERT INTO games(id, host_id, name, publica,  state) VALUES (5, 5, 'prueba5', false, 'LOBBY');
*/

INSERT INTO games(id, host_id, name, publica, start_time, state) VALUES (1, 1, 'prueba', true, '2023-11-11 21:16', 'LOBBY');
INSERT INTO games(id, host_id, name, publica, start_time, state) VALUES (2, 2, 'prueba2', true, '2023-11-11 21:16', 'LOBBY');
INSERT INTO games(id, host_id, name, publica, start_time, state, player_id) VALUES (3, 3, 'prueba3', true, '2023-11-11 21:16', 'OVER', 2);
INSERT INTO games(id, host_id, name, publica, start_time, state) VALUES (4, 1, 'prueba4', false, '2023-11-11 21:16', 'LOBBY');
INSERT INTO games(id, host_id, name, publica, start_time, state) VALUES (5, 5, 'prueba5', false, '2023-11-11 21:16', 'IN_PROGRESS');


INSERT INTO games_players(game_id, players_id) VALUES (1,2), (1,3);
INSERT INTO games_players(game_id, players_id) VALUES (3,4), (3,5);
INSERT INTO games_players(game_id, players_id) VALUES (5,4), (5,6);

/*Invitaciones de amistad*/
INSERT INTO invitations(id, game_id, is_accepted, player_source_id, player_target_id, discriminator) VALUES (1, null, false, 4, 5, 'FRIENDSHIP');
INSERT INTO invitations(id, game_id, is_accepted, player_source_id, player_target_id, discriminator) VALUES (2, null, false, 4, 6, 'FRIENDSHIP');
INSERT INTO invitations(id, game_id, is_accepted, player_source_id, player_target_id, discriminator) VALUES (3, null, false, 4, 1, 'FRIENDSHIP');
INSERT INTO invitations(id, game_id, is_accepted, player_source_id, player_target_id, discriminator) VALUES (4, null, false, 4, 2, 'FRIENDSHIP');
INSERT INTO invitations(id, game_id, is_accepted, player_source_id, player_target_id, discriminator) VALUES (7, null, false, 6, 1, 'FRIENDSHIP');
/*Invitaciones para jugar una partida*/
INSERT INTO invitations(id, game_id, is_accepted, player_source_id, player_target_id, discriminator) VALUES (5, 5, false, 5, 4, 'GAME');
INSERT INTO invitations(id, game_id, is_accepted, player_source_id, player_target_id, discriminator) VALUES (6, 5, false, 5, 6, 'GAME');

INSERT INTO players_friends(friend_id, player_id) VALUES (1,2), (2,1), (1,3), (3,1), (1,6), (6,1), (1,5), (5,1), (1,4), (4,1);