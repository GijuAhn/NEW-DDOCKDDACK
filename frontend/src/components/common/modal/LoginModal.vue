<template>
  <div>
    <div id="login-modal">
      <div id="close-button">
        <img
          :src="require(`@/assets/images/close-button.svg`)"
          width="18"
          height="18"
          id="close-icon"
          @click="setCurrentModalAsync"
        />
      </div>

      <div id="game-title">
        <span>똑딱</span>
      </div>
      <div id="login-button">
        <img
          class="kakao-btn"
          :src="require(`@/assets/images/kakao-login-btn.png`)"
          @click="kakaoLogin"
        />
        <img
          class="google-btn"
          :src="require(`@/assets/images/google-login-btn.png`)"
          @click="googleLogin"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
const store = useStore();
const kakaoLoginUrl = process.env.VUE_APP_KAKAO_LOGIN_PATH;
const googleLoginUrl = process.env.VUE_APP_GOOGLE_LOGIN_PATH;

const kakaoLogin = () => {
  window.location.replace(kakaoLoginUrl);
};
const googleLogin = () => {
  window.location.replace(googleLoginUrl);
};

const setCurrentModalAsync = (value) => {
  if (value === "then") {
    store.dispatch("commonStore/setCurrentModalAsync", {
      name: "reportComplete",
      data: "",
    });
  } else {
    store.dispatch("commonStore/setCurrentModalAsync", "");
  }
};
</script>

<style scoped>
#login-modal {
  background-color: white;
  width: 377px;
  height: 426px;
  border-radius: 10px;
  text-align: center;
}

#login-modal span {
  font-family: "Gugi-Regular";
  font-size: 96px;
}

#close-button {
  text-align: right;
  margin-right: 13px;
  margin-top: 50px;
}

#game-title {
  margin-top: 45px;
}

#close-icon {
  margin-top: 15px;
}

.kakao-btn {
  margin-top: 75px;
}

.google-btn {
  margin-top: 10px;
}

img:hover {
  cursor: pointer;
}
</style>
