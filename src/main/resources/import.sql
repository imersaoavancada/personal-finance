-- This file allow to write SQL commands that will be emitted in test and dev.
INSERT INTO banks (code, name) VALUES
  ('000', 'Banco Vazio'),
  ('999', 'Banco Cheio');

INSERT INTO accounts (name, account_type, bank_id, branch, account_number, credit_limit) VALUES
  ('Conta Corrente Exemplo', 'CHECKING', 1, '0001', '0001', 1000000),
  ('Conta Poupança Exemplo', 'SAVINGS' , 1, '0001', '0002', 0);
