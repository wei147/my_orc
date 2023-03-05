<template>
  <el-row :gutter="10">
    <el-col :xs="8" :sm="6" :md="4" :lg="3" :xl="1">
      <div class="grid-content bg-purple"></div>
    </el-col>
    <el-col :xs="4" :sm="6" :md="8" :lg="9" :xl="11" :offset="2">
      <!-- 上传图片区域 -->
      <div class="Left-area">
        <div class="grid-content bg-purple-light">
          <el-upload class="upload-demo" drag action=" " list-type="picture" :on-change="handleChange"
            :auto-upload="false" :before-upload="beforeAvatarUpload" :limit="limit" :on-exceed="handleExceed"
            :multiple="false">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip">只能上传jpg/png文件,且不超过2M</div>
          </el-upload>
        </div>
      </div>
      <el-row :gutter="20">
        <el-col :span="12" :offset="17">
          <div class="middle-area"></div>
          <div class="grid-content bg-purple">
            <el-button style="margin-top: 10px;" type="primary" @click="submitUpload" :loading="loading">
              提取文本<i class="el-icon-arrow-right el-icon--right"></i>
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-col>
    <el-col :xs="4" :sm="6" :md="8" :lg="9" :xl="11">
      <!-- 文本框区域 -->
      <div class="right-area">
        <div class="grid-content bg-purple">
          <el-input type="textarea" :show-word-limit="true" :rows="10" placeholder="提取文本内容" v-model="text">
          </el-input>
        </div>
      </div>
    </el-col>
    <el-col :xs="8" :sm="6" :md="4" :lg="3" :xl="1">
      <div class="grid-content bg-purple-light"></div>
    </el-col>
  </el-row>

  <!-- 消息提醒区 -->
  <!-- <el-button :plain="true" @click="open2">成功</el-button>
  <el-button :plain="true" @click="open3">警告</el-button>
  <el-button :plain="true" @click="open1">消息</el-button>
  <el-button :plain="true" @click="open4">错误</el-button> -->
  <!-- 原生vue上传文件的写法 -->
  <!-- <div>
    <input type="file" @change="onFileChange">
    <button @click="submitUpload">上传</button>
    <img v-if="imageUrl" :src="imageUrl" alt="Uploaded Image">
  </div> -->
</template>

<script>
  // import MyTip from './components/MyTip.vue'
  import axios from 'axios'
  export default {
    data() {
      return {
        imageUrl: "",
        fileList: [],
        text: '',
        limit: 1,
        loading: false
      }
    },
    //注册局部组件
    components: {},
    methods: {
      //通过onchanne触发方法获得文件列表
      handleChange(file, fileList) {
        this.fileList = fileList;
        console.log(fileList)
      },
      async submitUpload() {
        this.loading = true
        const formData = new FormData()
        // 这里的file要对应controller的@RequestParam 参数名
        this.fileList.forEach(item => {
          //文件信息中raw才是真的文件
          formData.append("file", item.raw);
        })
        try {
          axios.post('http://localhost:8000/ocr/upload/file', formData).then(res => {
            console.log(res);
            if (res.data.status == 10000) {
              this.open2()
              this.text = res.data.data
              this.loading = false
            } else {
              this.open4()
            }
          })
        } catch (error) {
          console.error(error)
        }
      },
      handleExceed() {
        this.open3()
      },
      beforeAvatarUpload(file) {
        // const isJPG = file.type === 'image/jpeg';
        const isLt2M = file.size / 1024 / 1024 < 2;

        // if (!isJPG) {
        //   this.$message.error('上传头像图片只能是 JPG 格式!');
        // }
        if (!isLt2M) {
          this.$message.error('上传图片大小不能超过 2MB!');
        }
        // return isJPG && isLt2M;
        console.log("beforeAvatarUpload");
        return isLt2M;
      },
      open1() {
        this.$message('这是一条消息提示');
      },
      open2() {
        this.$message({
          message: '提取成功',
          type: 'success'
        });
      },

      open3() {
        this.$message({
          message: '只能上传一张图片喔',
          type: 'warning'
        });
      },

      open4() {
        this.$message.error('提取失败');
      }
    },
  }
</script>

<style>
  .el-col {
    border-radius: 4px;
  }

  /* .bg-purple-dark {
    background: #99a9bf;
  }

  .bg-purple {
    background: #d3dce6;
  }

  .bg-purple-light {
    background: #e5e9f2;
  }

  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  } */

  .Left-area {
    margin-top: 20%;
  }

  .middle-area {
    margin-bottom: -20%;
  }

  .right-area {
    margin-top: 20%;
    margin-right: 20%;
  }

  .upload-demo {
    background-color: #fff;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    box-sizing: border-box;
    width: 400px;
    height: 220px;
    text-align: center;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
</style>