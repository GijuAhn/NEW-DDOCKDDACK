<template>
  <div class="user-info">
    <div id="profile">
      <div id="profileImg" style="float: left">
        <img
          :src="`${IMAGE_PATH}/${myProfile.profile}`"
          alt=""
          id="image"
          :key="updateImage"
        />
        <div id="div_modifyImg">
          <input
            type="file"
            @change="imgUploadEvent"
            accept=".jpg,.jpeg,.png"
            id="imgInput"
            style="display: none"
          />
          <label for="imgInput">
            <img
              :src="require(`@/assets/images/modify-profile-img.png`)"
              id="modifyProfile"
            />
          </label>
        </div>
      </div>
    </div>
    <div id="nameAndEmail">
      <div id="nickname" v-if="!save">
        <span id="name">{{ name }}</span>
        <span @click="modifyNameInput">
          <img
            :src="require(`@/assets/images/modify-name.png`)"
            alt="이름수정버튼"
            class="modify"
            id="btn_modifyName"
          />
        </span>
      </div>
      <div v-else>
        <form id="modifyInput" @submit="modifyName" onsubmit="return false">
          <input type="text" v-model="name" /><br />
          <button type="button" @click="modifyName">수정</button>
        </form>
        <span v-if="checkNickname" id="checkNickname">
          2~15글자내외, 특수문자 사용 불가능
        </span>
      </div>

      <div id="email">
        <div style="float: left">
          <span>{{ myProfile.email }}</span>
        </div>
        <div
          id="etcSection"
          v-click-outside-element="onClickOutside"
          style="float: left"
        >
          <div id="etcButton" @click="open">
            <img
              :src="require(`@/assets/images/etc-button.png`)"
              alt="기타버튼"
              class="etc"
            />
          </div>
          <div id="etc" v-show="state">
            <div>
              <span @click="withdrawal">회원 탈퇴</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { useStore } from "vuex";
import { computed, ref } from "vue";
import process from "process";

const store = useStore();
const api = apiInstance();
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

const accessToken = computed(() => store.state.memberStore.accessToken);
const myProfile = computed(() => store.state.memberStore.memberInfo).value;

const maxSize = 2 * 1024 * 1024;
const state = ref(false);
const updateImage = ref(0);

let name = myProfile.nickname;
let save = ref(false);
let checkNickname = ref(false);

const modifyNameInput = () => {
  //form형태로 변경
  console.log("click ", save.value);
  console.log("profile ", myProfile);
  save.value = !save.value;
};

// let memberReq = ref();

let reg_nickname = /^[A-z가-힣0-9_-]{2,15}$/;
const modifyName = () => {
  if (reg_nickname.test(name)) {
    api
      .put(
        `/api/members/nickname`,
        {
          nickname: name,
        },
        { headers: { "access-token": accessToken.value } }
      )
      .then(() => {
        console.log("성공");
        myProfile.value.nickname = name;
      })
      .catch((err) => {
        console.log("err ", err);
        if (err.response.status === 401) {
          alert("로그인 후 이용해주세요.");
        }
      });
    save.value = !save.value;
  } else {
    console.log("닉네임 규칙에 맞게");
    if (!checkNickname.value) {
      checkNickname.value = !checkNickname.value;
    }
  }
};

const imgUploadEvent = (e) => {
  modifyProfileImg(e.target.files);
};

let reg_img = ["jpg", "jpeg", "png"];
const modifyProfileImg = (f) => {
  const modifyImgName = f[0].name;

  let formData = new FormData();
  formData.append("profileImg", f[0]);

  const ext = modifyImgName.split(".").pop().toLowerCase();
  if (reg_img.includes(ext) && f[0].size < maxSize) {
    api
      .put(`/api/members/profile`, formData, {
        headers: { "access-token": accessToken.value },
      })
      .then((response) => {
        myProfile.profile = response.data;
      })
      .catch((err) => {
        if (err.response.status === 401) {
          alert("로그인 후 이용해주세요.");
          window.location.assign(`/`);
        }
      });
  } else {
    alert("2MB이하의 jpg, jpeg, png 이미지만 가능합니다!");
    console.log("이미지 규칙에 맞게");
  }
};

const withdrawal = () => {
  if (confirm("정말 탈퇴 하시겠습니까??") == true) {
    api
      .delete(`/api/members`, {
        headers: { "access-token": accessToken.value },
      })
      .then(() => {
        window.location.assign(`/`);
      })
      .catch(() => {
        // alert("회원 탈퇴에 실패했습니다.");
        window.location.assign(`/`);
      })
      .finally(() => {
        store.state.memberStore.$reset;
      });
  } else {
    return;
  }
};

const onClickOutside = () => {
  state.value = false;
};

const open = () => {
  state.value = !state.value;
};
</script>

<style scoped>
input {
  outline: none;
  border-radius: 5px;
  border: 2px solid black;
  font-size: 36px;
  font-family: "NanumSquareRoundB";
  padding: 5 10px;
  height: 46px;
  width: 300px;
  margin-bottom: 5px;
}

#etcSection {
  position: relative;
  left: 5%;
}

#nameAndEmail {
  /* position: absolute; */
  top: 22%;
}
#image {
  border-radius: 75%;
  box-shadow: 0 0px 3px #999;
  width: 150px;
  height: 150px;
  margin-left: auto;
  margin-right: auto;
}
.user-info {
  height: 290px;
  display: table;
}
.user-info > div {
  display: table-cell;
  vertical-align: middle;
}
#profileImg {
  margin-right: 30px;
  position: relative;
}
#modifyInput {
  margin-bottom: 20px;
  width: 350px;
  /* position: absolute; */
}
#name {
  font-size: 48px;
}
.modify {
  width: 20%;
  height: 20%;
}
#btn_modifyName {
  display: none;
}
#div_modifyImg {
  display: none;
  position: absolute;
  top: 50%;
  left: 52%;
  transform: translate(-50%, -50%);
}
#nickname:hover #btn_modifyName {
  width: 30px;
  height: 30px;
  display: inline;
  cursor: pointer;
}
#profileImg:hover #div_modifyImg,
#modifyProfile {
  display: inline;
  cursor: pointer;
  width: 50px;
  height: 50px;
}
#checkNickname {
  color: crimson;
}

#email {
  font-size: 20px;
  margin-top: 5px;
  position: relative;
}

#btn_withdrawal {
  display: inline-block;
  cursor: pointer;
  color: #666666;
  font-size: 15px;
  padding-top: 5px;
  /* padding: 12px 0px; */
}

#deleteMember {
  margin-bottom: 10px;
}

#email #deleteMember {
  position: relative;
}

button {
  background-color: transparent;
  /* border: none; */
  border-color: black;
  color: black;
  padding: 5px 10px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 14px;
  cursor: pointer;
  border-radius: 4px;
}

#etc-button {
  position: absolute;
  /* top: 0px;
  left: 290px;
  width: 30px;
  height: 30px; */
  border-radius: 50%;
  text-align: center;
}
#etc-button:hover {
  background-color: #d9d9d9;
  cursor: pointer;
  transition: 0.3s;
}
.etc {
  width: 20px;
  height: 20px;
  /* top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);  */
}

#etc {
  position: absolute;
  top: 30px;
  /* left: 290px; */
  background-color: white;
  width: 130px;
  box-shadow: 0 0 10px #8b8b8b;
  z-index: 1;
  border-radius: 10px;
  padding: 10px 0;
  display: block;
}
#etc :hover {
  cursor: pointer;
}
#etc span {
  font-size: 16px;
  display: block;
  padding: 10px;
}
#etc span:hover {
  background-color: #d9d9d9;
}
</style>
