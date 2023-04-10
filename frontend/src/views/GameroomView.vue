<template>
  <div id="session">
    <modal-frame v-if="currentModal.length !== 0" />
    <div id="session-header">
      <span id="session-title">
        {{ room.gameTitle }} [방 코드 - {{ room.pinNumber }}]
      </span>
      <span @click="linkShare">
        <img
          src="https://developers.kakao.com/assets/img/about/logos/kakaotalksharing/kakaotalk_sharing_btn_medium.png"
          id="kakaoShareButton"
        />
        <span> 공유하기</span></span
      >
    </div>

    <div id="main-container">
      <capture-video :stream-manager="openviduInfo.publisher" />

      <div id="left-section">
        <div id="gameInfoSection">
          <div v-if="intro" id="introSection">
            <span id="intro">게임 시작!</span>
          </div>
          <div v-if="isStart && !isEnd">
            <div id="gameImageSection">
              <img
                class="game-image"
                :src="`${IMAGE_PATH}/${room.gameImages[round - 1].gameImage}`"
              />
            </div>

            <div id="gameCurrentSection">
              <span v-show="!isEnd"> {{ round }}/{{ roundSet }}</span>
              <span v-show="!isEnd"> {{ timerCount }} </span>
            </div>
          </div>

          <div v-if="!isStart && !intro">
            <div id="gameWaitSection">
              <div id="relative">
                <img src="@/assets/images/gameWait.png" />
                <div id="Desc">
                  <span id="largeFont">잠시만 기다려주세요</span>
                  <br />
                  <br />
                  <span id="smallFont">
                    {{ openviduInfo.subscribers.length + 1 }}명 참가중...
                  </span>
                  <br />
                  <br />
                  <div>
                    <button
                      v-if="isHost"
                      v-show="!isStart"
                      @click="play"
                      style="float: right"
                    >
                      시작하기!
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="isEnd">
            <div id="gameResultSection">
              <div id="relative">
                <img id="result_1" src="@/assets/images/gameResult_1.png" />
                <img id="result_2" src="@/assets/images/gameResult_2.png" />
                <div id="Desc">
                  <button @click="setCurrentModalAsync(`bestcutUpload`)">
                    내 사진 보기
                  </button>
                  <button @click="setCurrentModalAsync(`finalResult`)">
                    최종 결과
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div id="my-video">
          <user-video
            :stream-manager="openviduInfo.publisher"
            :captureMode="captureMode"
            :isHost="isHost"
            :isSub="false"
          />
        </div>
      </div>
      <div id="right-section">
        <div
          id="video-container"
          :class="{
            grid1: 1 === openviduInfo.subscribers.length,
            grid2: 2 === openviduInfo.subscribers.length,
            grid3: 3 === openviduInfo.subscribers.length,
            grid4: 4 === openviduInfo.subscribers.length,
            grid5: 5 === openviduInfo.subscribers.length,
            grid6: 6 === openviduInfo.subscribers.length,
            grid7: 7 === openviduInfo.subscribers.length,
            grid8: 8 === openviduInfo.subscribers.length,
            grid9: 9 === openviduInfo.subscribers.length,
            grid10: 10 === openviduInfo.subscribers.length,
            grid11: 11 === openviduInfo.subscribers.length,
            grid12: 12 === openviduInfo.subscribers.length,
          }"
        >
          <user-video
            v-for="sub in openviduInfo.subscribers"
            :key="sub.stream.connection.connectionId"
            :stream-manager="sub"
            :isSub="true"
          />
        </div>
      </div>
    </div>

    <div id="button-container">
      <button
        class="btn-video-control"
        @click="pubAudioOff(openviduInfo.publisher)"
      >
        <img
          v-if="isPubAudioEnable"
          :src="require(`@/assets/images/microphone-on.png`)"
        />
        <span v-if="isPubAudioEnable">음소거</span>
        <img
          v-if="!isPubAudioEnable"
          :src="require(`@/assets/images/microphone-off.png`)"
        />
        <span v-if="!isPubAudioEnable">음소거 해제</span>
      </button>
      <button
        class="btn-video-control"
        @click="pubVideoOff(openviduInfo.publisher)"
      >
        <img
          v-if="isPubVideoEnable"
          :src="require(`@/assets/images/camera-on.png`)"
        />
        <span v-if="isPubVideoEnable">비디오 중지</span>
        <img
          v-if="!isPubVideoEnable"
          :src="require(`@/assets/images/camera-off.png`)"
        />
        <span v-if="!isPubVideoEnable">비디오 시작</span>
      </button>
      <button class="btn-close" @click="leaveSession">
        <img
          :src="require(`@/assets/images/close.png`)"
          width="18"
          height="18"
        />
      </button>
    </div>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { computed, onBeforeMount, ref } from "@vue/runtime-core";
import { useRoute } from "vue-router";
import { OpenVidu } from "openvidu-browser";
import UserVideo from "@/components/Gameroom/item/UserVideo.vue";
import CaptureVideo from "@/components/Gameroom/item/CaptureVideo.vue";
import { useStore } from "vuex";
import router from "@/router/index.js";
import process from "process";
import ModalFrame from "@/components/common/ModalFrame";
import html2canvas from "html2canvas";
import captureSound from "@/assets/sounds/capture_sound.mp3";
import backgroundSound from "@/assets/sounds/background_sound.mp3";

const currentModal = computed(() => store.state.commonStore.currentModal);
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;
const api = apiInstance();
const route = useRoute();
const store = useStore();

const accessToken = computed(() => store.state.memberStore.accessToken);
const nickname = ref(undefined);
const tempNickname = computed(
  () => store.state.memberStore.memberInfo.nickname
);
const openviduInfo = ref({
  // OpenVidu objects
  OV: undefined,
  session: undefined,
  mainStreamManager: undefined,
  publisher: undefined,
  subscribers: [],
});

// room object
const room = ref({
  pinNumber: undefined,
  gameId: undefined,
  gameTitle: undefined,
  gameDescription: undefined,
  gameImages: undefined,
});
const timeSet = 5;
const roundSet = 3;
const timerCount = ref(timeSet);
const isStart = ref(false);
const round = ref(1);
const isHost = ref(false);
const isEnd = ref(false);
const resultMode = ref(false);
const captureMode = ref(false);
const result = ref([]);
const resultImages = ref([]);
const winner = ref([]);
const intro = ref(false);
const isPubVideoEnable = ref(true);
const isPubAudioEnable = ref(true);
const captureAudio = new Audio(captureSound);
const backgroundAudio = new Audio(backgroundSound);

onBeforeMount(() => {
  //access-token 없으면 닉네임 입력 받도록 수정 필요
  if (!accessToken.value) {
    do {
      nickname.value = prompt("닉네임을 입력해주세요.");
      if (nickname.value == null) {
        router.replace("/");
      }
    } while (nickname.value.trim() == "");
  } else {
    nickname.value = tempNickname.value;
  }
  api
    .post(`/api/game-rooms/${route.params.pinNumber}`, {
      nickname: nickname.value,
    })
    .then((res) => {
      openviduInfo.value.OV = new OpenVidu();
      // openviduInfo.value.OV.enableProdMode();
      openviduInfo.value.session = openviduInfo.value.OV.initSession();
      // On every new Stream received...
      openviduInfo.value.session.on("streamCreated", ({ stream }) => {
        const subscriber = openviduInfo.value.session.subscribe(stream);
        openviduInfo.value.subscribers.push(subscriber);
      });

      // On every Stream destroyed...
      openviduInfo.value.session.on("streamDestroyed", ({ stream }) => {
        const index = openviduInfo.value.subscribers.indexOf(
          stream.streamManager,
          0
        );
        if (index >= 0) {
          openviduInfo.value.subscribers.splice(index, 1);
        }
      });

      // On every asynchronous exception...
      openviduInfo.value.session.on("exception", ({ exception }) => {
        console.warn(exception);
      });

      openviduInfo.value.session.on("roundStart", async (signal) => {
        round.value = signal.data;
        if (signal.data == 1) {
          intro.value = true;
          setTimeout(() => {
            intro.value = false;
            isStart.value = true;
            const timer = setInterval(() => {
              timerCount.value--;
              if (timerCount.value === 0) {
                clearInterval(timer);
                capture(signal.data - 1);
                setCurrentModalAsync("intermediateResult");
                timerCount.value = timeSet;
                resultMode.value = true;
              }
            }, 1000);
          }, 3000);
        } else {
          const timer = setInterval(() => {
            timerCount.value--;
            if (timerCount.value === 0) {
              clearInterval(timer);
              capture(signal.data - 1);
              setCurrentModalAsync("intermediateResult");
              timerCount.value = timeSet;
              resultMode.value = true;
            }
          }, 1000);
        }
      });

      openviduInfo.value.session.on("roundResult", (signal) => {
        result.value = JSON.parse(signal.data);
        setTimeout(() => {
          resultMode.value = false;
          result.value.length = 0;
          setCurrentModalAsync("");
          if (round.value < roundSet && isHost.value) {
            //체크
            api.get(`/api/game-rooms/${route.params.pinNumber}/round`);
          } else if (isHost.value) {
            api.get(`/api/game-rooms/${route.params.pinNumber}/final`);
          }
        }, 5000);
      });

      openviduInfo.value.session.on("finalResult", async (signal) => {
        backgroundSoundOff();
        resultMode.value = true;
        isEnd.value = true;
        result.value = await JSON.parse(signal.data);
        setTimeout(() => {
          resultMode.value = false;
        }, 5000);
      });

      openviduInfo.value.session.on("signal:host", () => {
        isHost.value = true;
      });

      openviduInfo.value.session.on("signal:playMusic", () => {
        backgroundAudio.play();
      });

      if (!accessToken.value) {
        console.log(typeof res.data);
      }

      openviduInfo.value.session
        .connect(res.data.token, {
          clientData: nickname.value,
        })
        .then(() => {
          // --- 5) Get your own camera stream with the desired properties ---

          // Init a publisher passing undefined as targetElement (we don't want OpenVidu to insert a video
          // element: we will manage it on our own) and with the desired properties
          let publisher = openviduInfo.value.OV.initPublisher(undefined, {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: "640x480", // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
            mirror: true, // Whether to mirror your local video or not
          });

          // Set the main video in the page to display our webcam and store our Publisher
          openviduInfo.value.mainStreamManager = publisher;
          openviduInfo.value.publisher = publisher;

          // --- 6) Publish your stream ---

          openviduInfo.value.session.publish(openviduInfo.value.publisher);
          room.value.pinNumber = res.data.pinNumber;
          room.value.gameId = res.data.gameId;
          room.value.gameTitle = res.data.gameTitle;
          room.value.gameDescription = res.data.gameDescription;
          room.value.gameImages = res.data.gameImages;
          isHost.value = res.data.isHost;
        })
        .catch((error) => {
          console.log(
            "There was an error connecting to the session:",
            error.code,
            error.message
          );
        });
      window.addEventListener("beforeunload", leaveSession);
    })
    .catch((err) => {
      if (err.response.status === 400) {
        alert("이미 시작된 게임방입니다.");
      }
      if (err.response.status === 404) {
        alert("존재하지 않는 게임방입니다.");
      }
      if (err.response.status === 401) {
        if (err.response.data.message == "The maximum length is 13") {
          alert("방이 꽉 찼습니다.");
        } else {
          alert("중복 참가는 불가능 합니다.");
        }
      }
      router.replace("/");
    });
});

const leaveSession = () => {
  // --- 7) Leave the session by calling 'disconnect' method over the Session object ---
  if (openviduInfo.value.session) {
    let socketId = openviduInfo.value.session.connection.connectionId;
    if (isHost.value) {
      throwHost();
    }
    // 방에 유저가 있는 경우 멤버삭제
    if (openviduInfo.value.subscribers.length) {
      api
        .delete(
          `/api/game-rooms/${route.params.pinNumber}/sessions/${socketId}`
        )
        .catch((err) => {
          console.log(err);
        });
    } else {
      // 남아있는 유저가 나 혼자인 경우 방삭제
      api.delete(`/api/game-rooms/${route.params.pinNumber}`).catch((err) => {
        console.log(err);
      });
    }
    backgroundSoundOff();
    captureSoundOff();
    openviduInfo.value.session.disconnect();
    openviduInfo.value.session = undefined;
    openviduInfo.value.mainStreamManager = undefined;
    openviduInfo.value.publisher = undefined;
    openviduInfo.value.subscribers = [];
    openviduInfo.value.OV = undefined;
    openviduInfo.value = undefined;
    room.value = undefined;
    isStart.value = false;
    round.value = 1;
    isHost.value = false;
    isEnd.value = false;
    resultMode.value = false;
    captureMode.value = false;
  }

  // Remove beforeunload listener
  window.removeEventListener("beforeunload", leaveSession);
  router.replace("/");
};

const throwHost = () => {
  if (openviduInfo.value.subscribers.length) {
    const nextHost =
      openviduInfo.value.subscribers[0].stream.connection.connectionId;
    openviduInfo.value.session.signal({
      data: "",
      to: [nextHost],
      type: "host",
    });
  }
};

const play = () => {
  api
    .put(`/api/game-rooms/${route.params.pinNumber}`)
    .then(() => {
      openviduInfo.value.session.signal({
        type: "playMusic",
      });
    })
    .catch(() => {
      alert("게임시작에 실패 하였습니다.");
    });
};

const linkShare = async () => {
  window.Kakao.Link.sendDefault({
    objectType: "feed",
    content: {
      title: `${room.value.gameTitle}`,
      description: `${room.value.gameDescription}`,
      imageUrl: `${IMAGE_PATH}/${room.value.gameImages[0].gameImage}`,
      link: {
        mobileWebUrl: `https://i8a409.p.ssafy.io/gameroom/${room.value.pinNumber}`,
        webUrl: `https://i8a409.p.ssafy.io/gameroom/${room.value.pinNumber}`,
      },
    },
    buttons: [
      {
        title: "게임 하러가기 ",
        link: {
          mobileWebUrl: `https://i8a409.p.ssafy.io/gameroom/${room.value.pinNumber}`,
          webUrl: `https://i8a409.p.ssafy.io/gameroom/${room.value.pinNumber}`,
        },
      },
    ],
  });
};

const capture = async (index) => {
  captureAudio.play();
  let me = document.getElementById("myVideo2");
  captureMode.value = true;
  setTimeout(() => {
    captureMode.value = false;
  }, 500);
  html2canvas(me)
    .then((canvas) => {
      let myImg;
      const socketId =
        openviduInfo.value.publisher.session.connection.connectionId;
      const pinNumber = openviduInfo.value.publisher.session.sessionId;
      myImg = canvas.toDataURL("image/jpeg");
      let byteString = myImg.replace("data:image/jpeg;base64,", "");

      let param = {
        gameImage: room.value.gameImages[index].gameImage,
        memberGameImage: byteString,
      };
      api.post(`/api/game-rooms/${pinNumber}/${socketId}/images`, param);
      resultImages.value.push(myImg);
    })
    .catch((err) => {
      console.log(err);
    });
};

const setCurrentModalAsync = (what) => {
  if (what === "bestcutUpload") {
    store.dispatch("commonStore/setCurrentModalAsync", {
      name: what,
      data: [resultImages, openviduInfo.value.publisher, room],
    });
  } else if (what === "intermediateResult") {
    store.dispatch("commonStore/setCurrentModalAsync", {
      name: what,
      data: [round, result, isEnd, winner, openviduInfo.value.publisher],
    });
  } else if (what === "finalResult") {
    store.dispatch("commonStore/setCurrentModalAsync", {
      name: what,
      data: result,
    });
  } else if (what === "") {
    store.dispatch("commonStore/setCurrentModalAsync", "");
  }
};
const pubVideoOff = (video) => {
  isPubVideoEnable.value = !isPubVideoEnable.value;
  video.publishVideo(isPubVideoEnable.value);
};

const pubAudioOff = (video) => {
  isPubAudioEnable.value = !isPubAudioEnable.value;
  video.publishAudio(isPubAudioEnable);
};

const backgroundSoundOff = () => {
  backgroundAudio.pause();
  backgroundAudio.currentTime = 0;
};

const captureSoundOff = () => {
  captureAudio.pause();
  captureAudio.currenttime = 0;
};

// setCurrentModalAsync("intermediateResult"); //체크
</script>

<style scoped>
#session {
  background-color: black;
  color: #ffffff;
  height: 100vh;
}
#session-header {
  height: 35px;
  /* background-color: rgb(255, 150, 150); */
  display: flex;
  align-items: flex-end;
  padding: 0 12px;
}
#session-header span {
  font-size: 18px;
  font-family: "NanumSquareRoundR";
}
#session-header span:last-child {
  margin-left: auto;
}
#session-header span:last-child:hover {
  cursor: pointer;
}
#main-container {
  /* border: 1px solid red; */
  height: calc(100vh - 105px);
  display: flex;
  position: relative;
}
#left-section {
  width: 50%;
}
#gameInfoSection {
  /* background-color: rgb(104, 104, 0); */
  height: 50%;
  flex-direction: column;
  position: relative;
}
#gameImageSection,
#gameWaitSection,
#gameResultSection {
  border-radius: 10px;
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border: 1px solid #464646;
}
#intro {
  color: white;
  font-family: "Gugi-Regular";
  font-size: 80px;
  position: absolute;
  top: 150px;
  left: 300px;
}
#gameImageSection img {
  height: 100%;
  width: 100%;
  object-fit: contain;
}

#gameWaitSection,
#gameResultSection {
  background-color: #fdf8ec;
}

#gameWaitSection button {
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  height: 48px;
  background-color: #f08383;
  color: white;
  width: 120px;
}
#gameWaitSection button:hover {
  cursor: pointer;
}

#relative {
  height: 100%;
  width: 100%;
  position: relative;
}

#gameWaitSection #Desc {
  float: left;
  top: 50%;
  left: 45%;
  position: absolute;
  transform: translate(0, -50%);
}

#gameWaitSection #relative img {
  top: 50%;
  position: absolute;
  transform: translate(10%, -50%);
}

#gameResultSection #Desc {
  float: left;
  top: 50%;
  left: 30%;
  position: absolute;
  transform: translate(0, -50%);
}

#gameResultSection #relative #result_1 {
  top: 15%;
  left: 15%;
  position: absolute;
  transform: translate(-50%, -50%);
  height: 30%;
}

#gameResultSection #relative #result_2 {
  top: 70%;
  left: 85%;
  position: absolute;
  transform: translate(-50%, -50%);
  height: 70%;
}

#largeFont {
  color: black;
  font-size: 50px;
}

#smallFont {
  color: black;
  font-size: 30px;
}

#gameCurrentSection {
  position: absolute;
  top: 25px;
  left: 25px;
  display: flex;
  width: calc(100% - 50px);
}
#gameCurrentSection span:first-child {
  border: 2px solid white;
  background-color: black;
  font-size: 24px;
  color: white;
  border-radius: 24px;
  width: 90px;
  height: 36px;
  text-align: center;
  line-height: 36px;
}
#gameCurrentSection span:last-child {
  border: 2px solid white;
  background-color: black;
  font-size: 24px;
  color: white;
  border-radius: 24px;
  width: 36px;
  height: 36px;
  text-align: center;
  line-height: 36px;
  margin-left: auto;
}
#gameResultSection button {
  border-radius: 5px;
  border: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  height: 48px;
  background-color: #f08383;
  color: white;
  width: 140px;
  margin: 0 20px;
}
#gameResultSection button:hover {
  cursor: pointer;
}
#my-video {
  /* background-color: rgb(104, 0, 87); */
  height: 50%;
  flex-direction: column;
}
#my-video > * {
  height: 100%;
}
#right-section {
  width: 50%;
  /* background-color: rgb(0, 0, 99); */
}
#video-container {
  height: 100%;
  display: grid;
}
.grid1 {
  grid-template-columns: repeat(1, 1fr);
  grid-template-rows: repeat(1, 1fr);
}
.grid2 {
  grid-template-columns: repeat(1, 1fr);
  grid-template-rows: repeat(2, 1fr);
}
.grid3 {
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
}
.grid4 {
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
}
.grid5 {
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(3, 1fr);
}
.grid6 {
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(3, 1fr);
}
.grid7 {
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 1fr);
}
.grid8 {
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 1fr);
}
.grid9 {
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 1fr);
}
.grid10 {
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(4, 1fr);
}
.grid11 {
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(4, 1fr);
}
.grid12 {
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(4, 1fr);
}
#video-container > * {
  height: 100%;
}

#kakaoShareButton {
  width: 24px;
  height: 24px;
  vertical-align: middle;
}

.game-image {
  transform: scaleX(-1);
}
#button-container {
  height: 70px;
  /* background-color: rgb(150, 216, 255); */
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-video-control {
  border: 1px solid #464646;
  padding: 10px 0;
  background: none;
  color: white;
  width: 170px;
  height: 45px;
  border-radius: 45px;
}
.btn-video-control {
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-video-control img {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}
.btn-video-control span {
  font-size: 18px;
  font-family: "NanumSquareRoundR";
}
.btn-close {
  background-color: #db1f2e;
  width: 45px;
  height: 45px;
  border-radius: 45px;
}
.btn-video-control:hover,
.btn-close:hover {
  cursor: pointer;
}
#button-container button {
  margin: 0 10px;
}
</style>
