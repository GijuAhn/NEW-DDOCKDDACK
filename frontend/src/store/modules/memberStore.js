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
  },
  actions: {
    setTokensAsync({ commit }, accessToken) {
      commit("setToken", accessToken);
    },
    async userConfirm({ commit }, user) {
      // console.log(user,"^^");
      await login(
        //accessToken과 refreshToken이 생성되게
        user,
        ({ data }) => {
          console.log(data, "^^");
          if (data.status === 200) {
            // let accessToken = data["access-token"];
            // let refreshToken = data["refresh-token"];
            // console.log("login success token created!!!! >> ", accessToken, refreshToken);
            commit("setMemberInfo", data);

            // sessionStorage.setItem("access-token", accessToken); //변수에
            // sessionStorage.setItem("refresh-token", refreshToken); //cookie에
          } else {
            console.log("login 실패");
          }
        },
        (error) => {
          console.log(error);
        }
      );
    },

    async getMemberInfo({ commit, state, dispatch }) {
      let accessToken = state.accessToken;
      await findByAccessToken(
        accessToken,
        ({ data }) => {
          if (data.email !== "") {
            // console.log("getMemberInfo data >> ", data);
            commit("setMemberInfo", data);
          } else {
            console.log("유저 정보 없음!!!!");
          }
        },
        async (error) => {
          console.log("[토큰 만료]", error.response.status);
          dispatch("accesstokenReissue", "true");
        }
      );
    },

    async accesstokenReissue({ commit, state, store }, isAuthPage) {
      console.log("이슈 진입");
      await accesstokenRegeneration(
        ({ data }) => {
          if (data) {
            let accessToken = data;
            commit("setToken", accessToken);
          }
        },
        async (error) => {
          //AccessToken 갱신 실패시 refreshToken이 문제임 >> 다시 로그인해야함
          commit("setToken", "");
          console.log("Reissue 실패");
          if (error === 401 && isAuthPage) {
            console.log("갱신 실패");
            await logout(
              state.memberInfo.id,
              ({ data }) => {
                if (data.status === 200) {
                  console.log("리프레시 토큰 제거 성공");
                } else {
                  console.log("리프레시 토큰 제거 실패");
                }
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
  },

  async memberLogout({ commit }, accessToken) {
    await logout(
      accessToken,
      ({ data }) => {
        if (data.status === 200) {
          commit("setMemberInfo", null);
        } else {
          console.log("유저 정보 없음!!!!");
        }
      },
      (error) => {
        commit("setToken", "");
        console.log(error);
      }
    );
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
        } else {
          console.log("유저 정보 없음!!!!");
        }
      },
      (error) => {
        commit("setToken", "");
        console.log(error);
      }
    );
  },
};
