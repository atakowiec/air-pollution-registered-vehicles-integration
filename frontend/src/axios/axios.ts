import {Axios} from "axios";

const axiosInstance = new Axios({
  baseURL: "http://localhost:5000",
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
});

// interceptor to convert data to JSON before sending
axiosInstance.interceptors.request.use(config => {
  if (config.data && config.headers['Content-Type'] === 'application/json') {
    config.data = JSON.stringify(config.data);
  }
  return config;
});

// interceptor to convert data to JSON after receiving
axiosInstance.interceptors.response.use(response => {
  if (response.headers['content-type'] === 'application/json') {
    response.data = JSON.parse(response.data);
  }
  return response;
});

export const getApi = () => axiosInstance
