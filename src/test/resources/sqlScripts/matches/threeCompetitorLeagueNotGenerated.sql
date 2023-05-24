INSERT INTO leagues(id, gun_type, max_competitors, league_matches_generated, league_name)
VALUES (1, 'PISTOL', 3, false, 'PISTOL_LEAGUE_1');

INSERT INTO league_competitors(competitor_id, league_id)
VALUES (3, 1), (4, 1), (5, 1);