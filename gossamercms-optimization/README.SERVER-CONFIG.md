Application	How it is served	Where to enable compression
RESONANCE public website	Apache2 on Ubuntu	Apache2
Java API and files	Spring Boot	Spring Boot
Admin React application	Node.js	Node.js, or Apache2 if Apache is proxying it

I'd configure Apache2 first, because it can improve delivery of the public website immediately. Then enable compression for the Java API and admin application separately.
1. Enable Brotli and GZIP on Ubuntu Apache2
This will cover your public RESONANCE website and any other static files served directly by Apache.

First, check which modules are available:
apache2 -v
apache2ctl -M | grep -E 'brotli|deflate|headers'


Enable the modules:
sudo a2enmod deflate
sudo a2enmod brotli
sudo a2enmod headers


If a2enmod brotli reports that the module doesn't exist, your Apache installation doesn't currently have the Brotli module available. GZIP will still work.

Create a compression configuration:
sudo nano /etc/apache2/conf-available/compression.conf


Add:
<IfModule mod_deflate.c>
    # GZIP compression
    AddOutputFilterByType DEFLATE \
        text/html \
        text/plain \
        text/css \
        text/xml \
        text/javascript \
        application/javascript \
        application/json \
        application/xml \
        image/svg+xml
</IfModule>

<IfModule mod_brotli.c>
    # Brotli compression
    AddOutputFilterByType BROTLI_COMPRESS \
        text/html \
        text/plain \
        text/css \
        text/xml \
        text/javascript \
        application/javascript \
        application/json \
        application/xml \
        image/svg+xml
</IfModule>


These are Apache's supported compression filters. Apache negotiates the supported encoding with the browser. 
Apache HTTP Server Version 2.4+1


Enable the configuration:
sudo a2enconf compression


Test the configuration before restarting:
sudo apache2ctl configtest


If it reports Syntax OK:
sudo systemctl reload apache2


You now have GZIP and, if the module is available, Brotli enabled for Apache responses.
2. Enable GZIP on your Spring Boot Java API
Since your Java application serves files directly, enable compression in Spring Boot as well.

In application.yml:
server:
  compression:
    enabled: true
    min-response-size: 1024
    mime-types:
      - text/html
      - text/plain
      - text/css
      - text/xml
      - text/javascript
      - application/javascript
      - application/json
      - application/xml
      - image/svg+xml


This enables GZIP for qualifying responses from the embedded web server. Spring Boot supports this through its embedded server configuration. 
Home+1


Important: This is primarily useful for your API JSON responses and text-based files. Don't bother compressing PDFs, ZIPs, JPEGs, PNGs or WebP files because they're already compressed.

Spring Boot's standard compression setting doesn't enable Brotli. You can add Brotli separately if you have a specific reason to do so, but I'd start with GZIP here.
3. Enable compression on your Node.js admin application
If you're using Express to serve the React application, install the compression middleware:
npm install compression


Then in your Express application:
import compression from 'compression';

app.use(compression());


The Express compression middleware supports GZIP and Brotli on currently supported Node.js versions. 
Express.js


If Node.js is only building the React application and Apache serves the resulting static files, you don't need this middleware; Apache will already compress those files.
4. Test the compression
You can test each public endpoint independently.

Test Brotli:
curl -s -o /dev/null -D - \
  -H "Accept-Encoding: br" \
  https://yourdomain.com


Look for:
Content-Encoding: br


Test GZIP:
curl -s -o /dev/null -D - \
  -H "Accept-Encoding: gzip" \
  https://yourdomain.com


Look for:
Content-Encoding: gzip


Repeat against your Java API endpoint and admin website.

5. Your overall configuration
Public RESONANCE website
Apache2 — Brotli + GZIP
HTML, CSS, JavaScript, SVG


Spring Boot Java API
GZIP
JSON responses and text-based files


React Admin
Node.js — GZIP + Brotli
If Node.js directly serves the React application


One architectural consideration: If Apache is acting as a reverse proxy in front of either Spring Boot or Node.js, Apache can also compress those responses. In that case, avoid enabling compression independently at both layers, as you want only one layer to handle compression.

My recommendation is to implement Apache compression first, verify it, and then configure Spring Boot and Node.js where necessary.