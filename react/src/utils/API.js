import { SourceType } from "../redux/constants/ActionTypes";
import RESTCaller from "./restCaller.js";
import cleaner from "./cleaner";

import lscache from "lscache";

const recommendationServer = process.env.REACT_APP_SERVER_URL;
// const newsapiHeadlinesUrl = process.env.REACT_APP_NEWSAPI_HEADLINES;
const newsapiHeadlinesUrl = `${process.env.REACT_APP_SERVER_URL}/${process.env.REACT_APP_NEWSAPI_HEADLINES}`;
// const newsapiBaseUrl = process.env.REACT_APP_NEWSAPI_SEARCH;
const newsapiBaseUrl = `${process.env.REACT_APP_SERVER_URL}/${process.env.REACT_APP_NEWSAPI_SEARCH}`;

const LSCACHE_TIMEOUT = 10;

const API = ({
  method,
  reqUrl,
  reqOpts,
  data = {},
  source,
  cached = true,
  headers = {
    "content-type": "application/json",
    Accept: "application/json",
  },
}) => {
  let apiBaseUrl = "INVALID";
  if (source) {
    switch (source) {
      case SourceType.NEWSAPI_HEADLINES:
        apiBaseUrl = newsapiHeadlinesUrl;
        break;
      case SourceType.NEWSAPI_SEARCH:
        apiBaseUrl = newsapiBaseUrl;
        break;
      case SourceType.LIKED:
      case SourceType.RECOMMENDATIONS:
        const user = localStorage.getItem("user");
        if (!user) {
          return;
        }
        let { token = false } = user ? JSON.parse(user) : {};
        headers = token
          ? { ...headers, Authorization: `Bearer ${token}` }
          : headers;
        apiBaseUrl = recommendationServer;
        break;
      default:
        apiBaseUrl = recommendationServer;
        break;
    }
  } else {
    apiBaseUrl = recommendationServer;
  }

  const url = `${apiBaseUrl}${reqUrl}${reqOpts}`;
  if (method.toLowerCase() === "get") {
    if (cached) {
      const response = lscache.get(url);
      if (response) {
        return new Promise((resolve, reject) => {
          resolve(response);
        }).catch((error) => console.log(error));
      }
    }
  }
  // console.log('REST url: ', url, 'apiBaseUrl', apiBaseUrl, 'reqUrl', reqUrl, 'reqOpts', reqOpts)
  let args = { method, apiBaseUrl, data, headers };
  if (reqUrl) {
    args.reqUrl = reqUrl;
  }
  if (reqOpts) {
    args.reqOpts = reqOpts;
  }
  // console.log('args', args)
  return RESTCaller(args).then((response) => {
    // console.log("%c{res}", "color: green", " => ", response); // eslint-disable-line no-console
    switch (source) {
      case SourceType.NEWSAPI_HEADLINES:
      case SourceType.NEWSAPI_SEARCH:
        response.data = cleaner(response.data.articles);
      case SourceType.RECOMMENDATIONS:
        break;
      default:
        break;
    }
    lscache.set(url, response, LSCACHE_TIMEOUT);
    return new Promise((resolve, reject) => {
      resolve(response);
    }).catch((error) => console.log(error));
  });
};

export default API;
