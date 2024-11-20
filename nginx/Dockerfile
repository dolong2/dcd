FROM nginx:latest

COPY nginx.conf /etc/nginx/nginx.conf
COPY conf /etc/nginx/conf.d/
COPY certificates /etc/letsencrypt/live/

# Nginx 포트
EXPOSE 80
EXPOSE 443