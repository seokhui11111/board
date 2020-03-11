-- 더미 데이터
insert into BOARD(bno,name,password,title,content,re_ref,re_lev,re_seq)
 (select board_seq.nextVal,name,password,title,content,board_seq.currVal,re_lev,re_seq from board);