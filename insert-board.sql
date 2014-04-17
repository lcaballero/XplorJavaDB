insert into board
	   (id, game_id, user_id, name, state, active, date_added)
values
(
		uuid_generate_v4(),
		uuid_generate_v4(),
		uuid_generate_v4(),
		'first',
		1,
		1,
		clock_timestamp()
);
	   
