import router from "@/router";
import {
  login,
  findByAccessToken,
  accesstokenRegeneration,
  logout,
  withdrawalMember,
} from "@/api/member";

export const memberStore = {
  namespaced: true,
  state: {
    accessToken: "",
    memberInfo: {
      id: "",
      email: "",
      nickname: "",
      profile: "",
      role: "",
    },
  },
  getters: {
    getAccessToken(state) {
      return state.accessToken;
    },
  },
  mutations: {
    setToken(state, accessToken) {
      state.accessToken = accessToken;
    },
    setMemberInfo(state, memberInfo) {
      state.memberInfo = memberInfo;
    },
    setMemberNickname(state, name) {
      state.memberInfo.nickname = name;
    },
  },
  actions: {
    setTokensAsync({ commit }, accessToken) {
      commit("setToken", accessToken);
    },
    async userConfirm({ commit }, user) {
      await login(
        //accessToken과 refreshToken이 생성되게
        user,
        ({ data }) => {
          if (data.status === 200) {
            // let accessToken = data["access-token"];
            // let refreshToken = data["refresh-token"];
            commit("setMemberInfo", data);

            // sessionStorage.setItem("access-token", accessToken); //변수에
            // sessionStorage.setItem("refresh-token", refreshToken); //cookie에
          }
        },
        (error) => {
          console.log(error);
        }
      );
    },
    async getMemberInfo({ commit }) {
      await findByAccessToken(({ data }) => {
        if (data.email !== "") {
          commit("setMemberInfo", data);
        }
      });
    },

    async accesstokenReissue({ commit, state, store }, accessToken) {
      await accesstokenRegeneration(
        accessToken,
        (data) => {
          if (data) {
            let accessToken = data.headers["access-token"];
            commit("setToken", accessToken);
          }
        },
        async (error) => {
          //AccessToken 갱신 실패시 refreshToken이 문제임 >> 다시 로그인해야함
          commit("setToken", "");
          //isAuthPage
          if (error === 401) {
            await logout(
              state.memberInfo.id,
              ({ data }) => {
                data;
                // alert("RefreshToken 기간 만료!!! 다시 로그인해 주세요.");
                store.dispatch("commonStore/setCurrentModalAsync", {
                  name: "login",
                  data: "",
                });
                router.push({ name: "main" });
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    },
    async memberLogout({ commit }, accessToken) {
      await logout(
        accessToken,
        ({ data }) => {
          data;
          // if (data.status === 200) {
          //   console.log("200확인");
          // }
        },
        (error) => {
          console.log(error);
        }
      );
      commit("setMemberInfo", "");
      commit("setToken", "");
    },
    async nicknameModify({ commit }, name) {
      commit("setMemberNickname", name);
    },
  },

  // withdrawal = () => {
  //   console.log("탈퇴!");
  //   api
  //     .delete(`/api/members`, {
  //       headers: { "access-token": accessToken.value },
  //     })
  //     .then(() => {
  //       store.state.memberStore.accessToken = null;
  //       store.state.memberStore.memberInfo = {};
  //       window.location.assign(`/`);
  //     })
  //     .catch((err) => {
  //       console.log(err);
  //       window.location.assign(`/`);
  //     });
  async withdrawal({ commit }, accessToken) {
    await withdrawalMember(
      accessToken,
      ({ data }) => {
        commit("setToken", "");
        if (data.status === 200) {
          commit("setMemberInfo", null);
        }
      },
      (error) => {
        commit("setToken", "");
        console.log(error);
      }
    );
  },
};
