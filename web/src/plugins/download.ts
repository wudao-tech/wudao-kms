import axios from 'axios';
import {saveAs} from 'file-saver';
import errorCode from '@/utils/errorCode';
import { blobValidate } from '@/utils/ruoyi';
import { LoadingInstance } from 'element-plus/es/components/loading/src/loading';
import { globalHeaders } from '@/utils/request';

const baseURL = import.meta.env.VITE_APP_BASE_API;
let downloadLoadingInstance: LoadingInstance;
export default {
  async name(name, isDelete = true) {
    const url = baseURL + '/common/download?fileName=' + encodeURI(name) + '&delete=' + isDelete;
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: globalHeaders()
    }).then(async (res) => {
      const isBlob = await blobValidate(res.data);
      if (isBlob) {
        const blob = new Blob([res.data]);
        saveAs(blob, decodeURI(res.headers['download-filename']));
      } else {
        ElMessage.error('无效的会话，或者会话已过期，请重新登录。');
      }
    });
  },
  async oss(ossId: string | number) {
    const url = baseURL + '/resource/oss/download/' + ossId;
    downloadLoadingInstance = ElLoading.service({ text: '正在下载数据，请稍候', background: 'rgba(0, 0, 0, 0.7)' });
    try {
      const res = await axios({
        method: 'get',
        url: url,
        responseType: 'blob',
        headers: globalHeaders()
      });
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        const blob = new Blob([res.data], { type: 'application/octet-stream' });
        saveAs(blob, decodeURIComponent(res.headers['download-filename'] as string));
      } else {
        this.printErrMsg(res.data);
      }
      downloadLoadingInstance.close();
    } catch (r) {
      console.error(r);
      ElMessage.error('下载文件出现错误，请联系管理员！');
      downloadLoadingInstance.close();
    }
  },
  async zip(url: string, name: string) {
    url = baseURL + url;
    downloadLoadingInstance = ElLoading.service({ text: '正在下载数据，请稍候', background: 'rgba(0, 0, 0, 0.7)' });
    try {
      const res = await axios({
        method: 'get',
        url: url,
        responseType: 'blob',
        headers: globalHeaders()
      });
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        const blob = new Blob([res.data], { type: 'application/zip' });
        saveAs(blob, name);
      } else {
        this.printErrMsg(res.data);
      }
      downloadLoadingInstance.close();
    } catch (r) {
      console.error(r);
      ElMessage.error('下载文件出现错误，请联系管理员！');
      downloadLoadingInstance.close();
    }
  },
  async printErrMsg(data: any) {
    const resText = await data.text();
    const rspObj = JSON.parse(resText);
    const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default'];
    ElMessage.error(errMsg);
  }
};


export const exportJSON = (jsonData: any, filename: any) => {
  const jsonString = JSON.stringify(jsonData);
  const blob = new Blob([jsonString], {type: 'application/json'});
  saveAs(blob, filename);
}