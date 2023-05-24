INSERT INTO leagues(id, gun_type, max_competitors, competitors_count, expected_rounds_to_play, current_round_no, league_matches_generated, league_name)
VALUES (1, 'PISTOL', 3, 3, 2, 1, true, 'PISTOL_LEAGUE_1');

INSERT INTO league_competitors(competitor_id, league_id)
VALUES (3, 1), (4, 1), (5, 1);