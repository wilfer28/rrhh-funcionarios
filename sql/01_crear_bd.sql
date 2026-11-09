CREATE DATABASE IF NOT EXISTS rrhh_iudigital CHARACTER SET utf8mb4;
USE rrhh_iudigital;

CREATE TABLE tipo_documento (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE estado_civil (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE funcionarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tipo_documento_id INT NOT NULL,
  numero_identificacion VARCHAR(20) NOT NULL UNIQUE,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  estado_civil_id INT NOT NULL,
  sexo CHAR(1) NOT NULL,
  direccion VARCHAR(120),
  telefono VARCHAR(20),
  fecha_nacimiento DATE NOT NULL,
  CONSTRAINT fk_func_tipodoc FOREIGN KEY (tipo_documento_id) REFERENCES tipo_documento(id),
  CONSTRAINT fk_func_estcivil FOREIGN KEY (estado_civil_id) REFERENCES estado_civil(id)
);

CREATE TABLE grupo_familiar (
  id INT AUTO_INCREMENT PRIMARY KEY,
  funcionario_id INT NOT NULL,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  fecha_nacimiento DATE,
  rol VARCHAR(40) NOT NULL,
  CONSTRAINT fk_fam_func FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE
);

CREATE TABLE formacion_academica (
  id INT AUTO_INCREMENT PRIMARY KEY,
  funcionario_id INT NOT NULL,
  universidad VARCHAR(100) NOT NULL,
  nivel_estudio VARCHAR(50) NOT NULL,
  titulo VARCHAR(100) NOT NULL,
  CONSTRAINT fk_acad_func FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE
);
