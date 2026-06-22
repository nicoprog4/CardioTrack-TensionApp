# AI Repository Operating System

## Propósito

Este repositorio debe mantenerse con estándares profesionales de ingeniería de software.

El objetivo no es únicamente que el software funcione correctamente, sino demostrar:

- Calidad técnica
- Organización
- Mantenibilidad
- Escalabilidad
- Seguridad
- Capacidad de documentación
- Buenas prácticas de Git y GitHub
- Nivel profesional apto para entrevistas técnicas y revisión por reclutadores

Este repositorio debe ser tratado como si fuera evaluado por:

- Reclutadores
- Tech Leads
- Arquitectos de Software
- Ingenieros Senior
- Entrevistadores Técnicos
- Equipos de Desarrollo

---

# Principios Fundamentales

Priorizar siempre en este orden:

1. Seguridad
2. Correctitud
3. Mantenibilidad
4. Legibilidad
5. Escalabilidad
6. Rendimiento
7. Experiencia del desarrollador

Nunca sacrificar mantenibilidad por conveniencia temporal.

---

# Modo de Trabajo Obligatorio

Antes de realizar cualquier modificación:

1. Analizar todo el repositorio recursivamente.
2. Comprender la arquitectura existente.
3. Comprender cómo funciona realmente el proyecto.
4. Identificar tecnologías y dependencias.
5. Identificar módulos y componentes.
6. Identificar riesgos.
7. Identificar deuda técnica.
8. Generar un informe inicial.

No realizar cambios sin análisis previo.

No asumir funcionalidades inexistentes.

Trabajar únicamente sobre evidencia encontrada en el código.

---

# Fases de Trabajo

## Fase 1: Auditoría Técnica

Analizar:

- Arquitectura
- Dependencias
- Seguridad
- Organización
- Calidad de código
- Documentación
- Pruebas
- Configuración
- Integraciones
- Base de datos
- APIs
- Infraestructura

---

## Fase 2: Diagnóstico

Identificar:

- Riesgos
- Problemas
- Código duplicado
- Código muerto
- Dependencias obsoletas
- Problemas de arquitectura
- Problemas de mantenibilidad
- Problemas de seguridad

---

## Fase 3: Propuesta

Proponer:

- Mejoras
- Reestructuración
- Refactorizaciones justificadas
- Mejoras de documentación
- Mejoras de Git

---

## Fase 4: Implementación

Realizar cambios pequeños, seguros y trazables.

Evitar cambios masivos innecesarios.

---

## Fase 5: Documentación

Actualizar toda documentación afectada.

Nunca dejar documentación desactualizada.

---

## Fase 6: Git

Organizar commits profesionalmente.

---

## Fase 7: Revisión

Auditoría antes de commit.

---

## Fase 8: Publicación

Auditoría antes de push.

---

# Estándares de Calidad de Código

## Clean Code

Aplicar siempre que sea posible:

- Nombres descriptivos
- Funciones pequeñas
- Responsabilidad única
- Bajo acoplamiento
- Alta cohesión
- Código legible
- Complejidad controlada

Evitar:

- Código duplicado
- Funciones gigantes
- Variables ambiguas
- Dependencias innecesarias

---

## Principios SOLID

Aplicar SOLID cuando corresponda.

- Single Responsibility
- Open/Closed
- Liskov
- Interface Segregation
- Dependency Inversion

---

## Separación de Responsabilidades

Mantener separadas:

- Presentación
- Negocio
- Persistencia
- Configuración
- Infraestructura
- Integraciones

---

# Estándares de Arquitectura

Identificar arquitectura existente.

Ejemplos:

- MVC
- MVVM
- Hexagonal
- Clean Architecture
- Monolito
- Microservicios
- Capas tradicionales

No forzar arquitecturas que no encajen.

Proponer mejoras únicamente cuando aporten valor real.

---

# Estándares de Documentación

La documentación debe reflejar exactamente la implementación real.

Nunca inventar funcionalidades.

Nunca documentar características inexistentes.

---

# README Obligatorio

El README debe incluir:

1. Nombre del proyecto
2. Descripción
3. Problema que resuelve
4. Objetivos
5. Tecnologías utilizadas
6. Arquitectura
7. Requisitos
8. Instalación
9. Configuración
10. Ejecución
11. Uso
12. Estructura del proyecto
13. Funcionalidades
14. Capturas de pantalla
15. Diagramas
16. Mejoras futuras
17. Autor
18. Licencia

No utilizar descripciones genéricas.

---

# Carpeta Docs

Cuando corresponda crear:

docs/

- arquitectura.md
- instalacion.md
- funcionamiento.md
- estructura-proyecto.md
- api.md
- base-datos.md
- despliegue.md
- decisiones-tecnicas.md

---

# Estándares de Git

Utilizar Conventional Commits.

Permitidos:

feat:
fix:
docs:
refactor:
test:
style:
chore:
build:
ci:
perf:

---

## Ejemplos

feat: implementar autenticación JWT

fix: corregir validación de pacientes

docs: actualizar guía de instalación

refactor: simplificar lógica de terapias

test: agregar pruebas de integración

---

## Nunca usar

update

changes

final version

test

misc

wip

fixes

---

# Organización de Commits

Cada commit debe representar una única intención.

No mezclar:

- lógica
- documentación
- refactorización
- configuración

en un mismo commit si pueden separarse.

---

# Estrategia de Branches

Utilizar:

main

Para nuevas funcionalidades:

feature/nombre-funcionalidad

Para correcciones:

fix/nombre-correccion

Para documentación:

docs/nombre-documentacion

---

# Seguridad

Nunca permitir commits con:

- contraseñas
- API keys
- secretos
- certificados
- tokens
- archivos .env reales
- credenciales

Verificar antes de cada push.

---

# Dependencias

Revisar:

- dependencias sin uso
- dependencias vulnerables
- dependencias obsoletas

Proponer limpieza cuando sea posible.

---

# Estándares de Testing

Cuando existan pruebas:

- mantenerlas actualizadas
- evitar romperlas
- ampliar cobertura cuando sea necesario

Cuando no existan:

proponer estrategia de pruebas.

---

# Estructura Recomendada

Adaptar según la tecnología.

Estructura base:

project/

├── src/
├── tests/
├── docs/
├── assets/
├── config/
├── scripts/
├── .github/
├── README.md
├── LICENSE
├── CHANGELOG.md
└── .gitignore

Mantener la raíz limpia.

---

# Estándares Profesionales de GitHub

## Nombre del Repositorio

Utilizar nombres:

- descriptivos
- profesionales
- legibles

Formato recomendado:

nombre-del-proyecto

Ejemplos:

physical-therapy-management-system

bookzone-online-library

inventory-management-system

task-management-api

---

## Descripción del Repositorio

Generar siempre una descripción profesional.

Debe indicar:

- qué hace
- tecnología principal
- propósito

---

## Topics

Proponer topics relevantes.

Ejemplo:

java

spring-boot

mysql

rest-api

portfolio-project

software-engineering

---

## Licencia

Recomendar:

MIT

para proyectos personales y portafolio.

Apache 2.0

cuando sea conveniente.

Nunca dejar repositorios públicos sin licencia.

---

# Archivos Obligatorios

Todo repositorio debería contener:

README.md

LICENSE

.gitignore

CHANGELOG.md

.github/

Opcional según el proyecto:

CONTRIBUTING.md

SECURITY.md

CODE_OF_CONDUCT.md

---

# Versionado

Utilizar Semantic Versioning.

Formato:

MAJOR.MINOR.PATCH

Ejemplos:

1.0.0

1.1.0

1.1.1

---

# Pull Requests

Todo Pull Request debe incluir:

- Resumen
- Motivación
- Riesgos
- Archivos afectados
- Pruebas realizadas
- Capturas si aplica

---

# Releases

Antes de una release:

- Validar README
- Validar documentación
- Validar pruebas
- Actualizar CHANGELOG
- Revisar seguridad

---

# Checklist Antes de Commit

Verificar:

- Archivos modificados
- Archivos eliminados
- Archivos generados
- Secretos
- Calidad del código
- Impacto en documentación

---

# Checklist Antes de Push

Verificar:

- No existen secretos
- No existen contraseñas
- No existen tokens
- No existen certificados privados
- No existen builds innecesarios
- No existen archivos temporales
- README actualizado
- Documentación consistente
- Commits correctos

---

# Checklist Antes del Primer Push

Verificar:

- README profesional
- LICENSE
- CHANGELOG
- .gitignore
- Documentación
- Capturas
- Diagramas
- Nombre profesional del repositorio
- Descripción profesional
- Topics adecuados

---

# Requisitos de Portafolio

Asumir siempre que este repositorio será utilizado para:

- Conseguir empleo
- Conseguir prácticas
- Mostrar habilidades técnicas
- Mostrar buenas prácticas

El repositorio debe transmitir:

- Profesionalismo
- Organización
- Calidad
- Mantenibilidad
- Capacidad técnica
- Capacidad documental

---

# Entregables Esperados de la IA

Cuando se solicite una auditoría completa, entregar:

1. Resumen ejecutivo
2. Arquitectura detectada
3. Tecnologías identificadas
4. Riesgos encontrados
5. Problemas encontrados
6. Mejoras recomendadas
7. Estructura recomendada
8. README recomendado
9. Documentación recomendada
10. .gitignore recomendado
11. Estrategia de commits
12. Nombre recomendado para GitHub
13. Descripción recomendada para GitHub
14. Topics recomendados
15. Checklist final de publicación

Nunca omitir el análisis inicial.

Nunca inventar funcionalidades.

Siempre justificar decisiones importantes.

Priorizar calidad profesional y mantenibilidad a largo plazo.