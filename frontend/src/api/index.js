import axios from "axios";
import store from "@/store/index";
import router from "@/router";

// local vue api axios instance
function apiInstance() {
  const instance = axios.create({});
  instance.interceptors.request.use(
    function (config) {
      console.log();
      config.headers["access-token"] = store.state.memberStore.accessToken;
      return config;
    },
    function (error) {
      return Promise.reject(error);
    }
  );
  instance.interceptors.response.use(
    function (response) {
      return response;
    },
    async function (error) {
      const errorApi = error.config;
      if (error.response.status === 401 && errorApi.retry === undefined) {
        if (error.response.data.message === "login required") {
          return Promise.reject(error);
        }
        errorApi.retry = true;
        await accesstokenRegeneration();
        errorApi.headers["access-token"] = store.state.memberStore.accessToken;
        return await axios(errorApi);
      }
      if (
        error.response.status === 400 &&
        error.response.data.message === "invalid token"
      ) {
        alert("다시 로그인 해주세요.");
        router.replace("/");
        return Promise.reject(error);
      }
      return Promise.reject(error);
    }
  );

  async function accesstokenRegeneration() {
    await instance.get(`/api/token/refresh`).then((res) => {
      const accessToken = res.headers["access-token"];
      store.state.memberStore.accessToken = accessToken;
    });
  }
  return instance;
}

export { apiInstance };
