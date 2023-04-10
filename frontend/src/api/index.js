import axios from "axios";
// import { memberStore } from "@/store/modules/memberStore";

// local vue api axios instance
function apiInstance() {
  const instance = axios.create({
    // baseURL: "http://localhost:8080", //env로 설정 해줘야함
    // headers: {
    //   "Content-Type": "application/json;charset=utf-8",
    // },
    // headers: {
    //   "Content-Type": "multipart/form-data", //넣어도 되고 안넣어도 되고
    // },
  });
  return instance;
}

export { apiInstance };
