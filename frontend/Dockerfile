# Stage 1: Build the Angular application
FROM node:18 AS build

WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .

# Build the application for production
RUN npm run build -- --configuration production

# Stage 2: Serve the application from Nginx
FROM nginx:1.23-alpine

# Copy the built application from the build stage
COPY --from=build /app/dist/frontend /usr/share/nginx/html

# Copy the custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80
