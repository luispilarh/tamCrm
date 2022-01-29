INSERT INTO crmuser ( username, email, admin)
VALUES ( 'luispih', 'luispilarh@gmail.com', true)
ON CONFLICT DO NOTHING;