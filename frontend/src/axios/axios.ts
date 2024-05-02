import {Axios} from "axios";

const axiosInstance = new Axios({
  baseURL: "https://jsonplaceholder.typicode.com",
});

export const getApi = () => axiosInstance
