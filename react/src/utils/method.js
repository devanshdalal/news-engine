import isEmpty from "lodash/isEmpty";
import orderBy from "lodash/orderBy";
import api_response from "./response.json";

export const APIhandler = options => {
  let params = {};
  // get url params
  const routeParams = options.reqUrl.split("?");
  if (routeParams.length > 1) {
    let paramsStr = routeParams[1];
    paramsStr.split("&").map(data => {
      let keyValue = data.split("=");
      params[keyValue[0]] = keyValue[1] ? keyValue[1] : "";
      return "";
    });
  }

  // name: "dsds"
  // limit: "20"
  // skip: "0"
  // sort: "-season"
  return new Promise(async (resolve, reject) => {
    // console.log("params", params);
    console.log("api_response.length", api_response.length, params);

    if (api_response.length) {
      if (!isEmpty(params)) {
        let response = api_response;
        if (!isEmpty(params.name)) {
          response = response.filter(item =>
            item.name.toLowerCase().includes(params.name.toLowerCase())
          );
        }
        if (!isEmpty(params.region)) {
          response = response.filter(item =>
            item.neighbourhood
              .toLowerCase()
              .includes(params.region.toLowerCase())
          );
        }
        if (params.sort) {
          const [sort, order] = params.sort.split(",");

          response = orderBy(response, sort, order);
        }
        console.log("response", response);

        return resolve({
          data: response
        });
      } else {
        return resolve({
          data: api_response
        });
      }
    } else {
      return reject({});
    }
  });
};
