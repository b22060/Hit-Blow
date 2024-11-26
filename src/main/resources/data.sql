-- ユーザ情報を格納
INSERT INTO users (name) VALUES('User1');
INSERT INTO users (name) VALUES('User2');
INSERT INTO users (name) VALUES('CPU');

-- 試合情報を格納
INSERT INTO matches (userid1,userid2,usernum1,usernum2,judge,isActive) VALUES(1,2,'0123','2345','User1の勝利!','FALSE');
INSERT INTO matches (userid1,userid2,usernum1,usernum2,judge,isActive) VALUES(1,2,'5673','0123','User2の勝利!','FALSE');

-- 試合情報を格納(matchid1の情報)
INSERT INTO matchinfo (matchid,userid,guess,hit,blow,isActive) VALUES(1,1,'1234',0,3,'FALSE');
INSERT INTO matchinfo (matchid,userid,guess,hit,blow,isActive) VALUES(1,2,'9876',0,0,'FALSE');
INSERT INTO matchinfo (matchid,userid,guess,hit,blow,isActive) VALUES(1,1,'2345',4,0,'FALSE');

-- 試合情報を格納(matchid2の情報)
INSERT INTO matchinfo (matchid,userid,guess,hit,blow,isActive) VALUES(2,1,'1234',0,3,'FALSE');
INSERT INTO matchinfo (matchid,userid,guess,hit,blow,isActive) VALUES(2,2,'9876',1,1,'FALSE');
INSERT INTO matchinfo (matchid,userid,guess,hit,blow,isActive) VALUES(2,1,'9876',0,0,'FALSE');
INSERT INTO matchinfo (matchid,userid,guess,hit,blow,isActive) VALUES(2,2,'5673',4,0,'FALSE');
