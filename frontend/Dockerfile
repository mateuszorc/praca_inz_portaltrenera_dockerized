# FROM node:17-alpine AS builder
# ENV NODE_ENV production

# WORKDIR /app

# COPY package.json .
# COPY package-lock.json .
# RUN npm install --production
# # Copy app files
# COPY . .
# # Build the app
# RUN npm run build

# # Bundle static assets with nginx
# FROM nginx:1.21.0-alpine as production
# ENV NODE_ENV production

# COPY --from=builder /app/build /usr/share/nginx/html

# COPY nginx.conf /etc/nginx/conf.d/default.conf

# EXPOSE 80

# CMD ["nginx", "-g", "daemon off;"]

FROM node:17-alpine
WORKDIR /app

COPY package.json .
COPY package-lock.json .
COPY ./ ./
RUN npm install
CMD ["npm", "run", "start"]