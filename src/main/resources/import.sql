-- This file allow to write SQL commands that will be emitted in test and dev.
INSERT INTO banks (code, name) VALUES
  ('000', 'Banco Vazio'),
  ('999', 'Banco Cheio');

INSERT INTO histories (name, payment_date, amount) VALUES
  ('Histórico 1', '2025-01-01 12:00:00.000Z', 10000),
  ('Histórico 2', '2025-01-10 17:00:00.000Z', -5000),
  ('Histórico 3', '2025-01-11 17:00:00.000Z', -1000);
