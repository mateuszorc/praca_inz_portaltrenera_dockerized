
const { createProxyMiddleware } = require("http-proxy-middleware");
module.exports = function (app) {
  app.use(
    "/api",
    createProxyMiddleware({
      // target: "http://localhost:8080",   for reference
      target: "http://192.168.112.3:8080",
      changeOrigin: true,
    })
  );
};
