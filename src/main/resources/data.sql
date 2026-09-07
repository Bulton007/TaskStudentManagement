INSERT INTO countries (code, name)
VALUES
    ('KH', 'Cambodia'),
    ('JP', 'Japan'),
    ('VN', 'Vietnam')
ON CONFLICT (code) DO NOTHING;


INSERT INTO provinces (name, country_id)
SELECT 'Phnom Penh', id
FROM countries
WHERE code = 'KH'
ON CONFLICT (country_id, name) DO NOTHING;

INSERT INTO provinces (name, country_id)
SELECT 'Siem Reap', id
FROM countries
WHERE code = 'KH'
ON CONFLICT (country_id, name) DO NOTHING;

INSERT INTO provinces (name, country_id)
SELECT 'Battambang', id
FROM countries
WHERE code = 'KH'
ON CONFLICT (country_id, name) DO NOTHING;


INSERT INTO districts (name, province_id)
SELECT 'Chamkar Mon', id
FROM provinces
WHERE name = 'Phnom Penh'
ON CONFLICT (province_id, name) DO NOTHING;

INSERT INTO districts (name, province_id)
SELECT 'Daun Penh', id
FROM provinces
WHERE name = 'Phnom Penh'
ON CONFLICT (province_id, name) DO NOTHING;

INSERT INTO districts (name, province_id)
SELECT 'Sen Sok', id
FROM provinces
WHERE name = 'Phnom Penh'
ON CONFLICT (province_id, name) DO NOTHING;

INSERT INTO districts (name, province_id)
SELECT 'Siem Reap Municipality', id
FROM provinces
WHERE name = 'Siem Reap'
ON CONFLICT (province_id, name) DO NOTHING;