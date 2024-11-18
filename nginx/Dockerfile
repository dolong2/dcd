FROM nginx:latest

COPY nginx.conf /etc/nginx/nginx.conf
COPY conf /etc/nginx/conf.d/
COPY dcd-api.dolong2.co.kr /etc/letsencrypt/live/dcd-api.dolong2.co.kr/

# Nginx 포트
EXPOSE 80
EXPOSE 443