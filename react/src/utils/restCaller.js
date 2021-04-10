import axios from "axios";

const RESTCaller = ({
  method = "get",
  apiBaseUrl,
  reqUrl,
  reqOpts,
  data = {},
  headers = {
    "content-type": "application/json",
    Accept: "application/json"
  }
}) => {
  return new Promise(async (resolve, reject) => {
    let url = `${apiBaseUrl}`;
    if (reqUrl) {
      url = `${apiBaseUrl}/${reqUrl}`;
    }
    if (reqOpts) {
      url = `${url}?${reqOpts}`;
    }

    let options = {
      method,
      url,
      data,
      headers
    };
    if (method.toLowerCase() === "get") {
      delete options["data"];
    }
    // console.log('REST call:', url, ' ', apiBaseUrl, ' ', reqUrl, ' ', reqOpts)
    axios({ ...options })
      .then(response => {
        resolve(response);
      })
      .catch(error => {
        reject(error);
      });
  });
};

export default RESTCaller;
