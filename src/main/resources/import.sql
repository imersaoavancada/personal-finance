-- This file allow to write SQL commands that will be emitted in test and dev.
INSERT INTO banks (code, name) VALUES
  ('000', 'Banco Vazio'),
  ('999', 'Banco Cheio');

INSERT INTO accounts (name, account_type, bank_id, branch, account_number, credit_limit) VALUES
  ('Conta Corrente Exemplo', 'CHECKING', 1, '0001', '0001', 1000000),
  ('Conta Poupança Exemplo', 'SAVINGS' , 1, '0001', '0002', 0),
  ('Conta Investimento Exemplo', 'INVEST' , null, null, null, 0);

INSERT INTO histories (name, payment_date, amount, account_id) VALUES
  ('Histórico 1', '2025-01-01 12:00:00.000Z', 10000, 1),
  ('Histórico 2', '2025-01-10 17:00:00.000Z', -5000, 1),
  ('Histórico 3', '2025-01-11 13:00:00.000Z', -2000, 3),
  ('Histórico 4', '2025-01-15 16:00:00.000Z', -1000, null);
