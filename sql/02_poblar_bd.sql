USE rrhh_iudigital;

INSERT INTO tipo_documento (nombre) VALUES
('Cédula de ciudadanía'), ('Cédula de extranjería'), ('Pasaporte'), ('Tarjeta de identidad');

INSERT INTO estado_civil (nombre) VALUES
('Soltero(a)'), ('Casado(a)'), ('Unión libre'), ('Divorciado(a)'), ('Viudo(a)');

INSERT INTO funcionarios (tipo_documento_id, numero_identificacion, nombres, apellidos, estado_civil_id, sexo, direccion, telefono, fecha_nacimiento) VALUES
(1, '1017000001', 'Carlos Andrés', 'Gómez Restrepo', 2, 'M', 'Calle 50 #45-10, Medellín', '3001112233', '1985-03-14'),
(1, '43000002', 'María Fernanda', 'López Zapata', 1, 'F', 'Carrera 70 #30-22, Medellín', '3114445566', '1992-11-02');

INSERT INTO grupo_familiar (funcionario_id, nombres, apellidos, fecha_nacimiento, rol) VALUES
(1, 'Laura', 'Pérez', '1987-06-20', 'Esposa'),
(1, 'Santiago', 'Gómez Pérez', '2015-01-09', 'Hijo');

INSERT INTO formacion_academica (funcionario_id, universidad, nivel_estudio, titulo) VALUES
(1, 'Universidad de Antioquia', 'Pregrado', 'Ingeniero Administrativo'),
(2, 'Universidad Nacional', 'Especialización', 'Gerencia del Talento Humano');
