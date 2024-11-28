FROM python:3.13-slim

# Configurar la aplicación para mostrar los logs en tiempo real
ENV PYTHONUNBUFFERED True

# Establecer el directorio de trabajo
ENV APP_HOME /app
WORKDIR $APP_HOME
COPY . ./

# Instalar dependencias
RUN pip install --no-cache-dir Flask gunicorn requests

# Ejecutar el servidor gunicorn en el puerto definido por la variable $PORT
CMD exec gunicorn --bind :$PORT --workers 1 --threads 8 --timeout 0 main:app
