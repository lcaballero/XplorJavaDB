create table if not exists board
(
		id uuid not null,
		game_id uuid not null,
		user_id uuid not null,
		name text,
		date_added timestamp without time zone,
		state integer,
		active integer,
		constraint board_pkey primary key (id)
)
