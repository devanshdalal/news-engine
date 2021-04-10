import { ActionTypes } from "../constants/ActionTypes";

const initialState = { loadState: false };
const loadingReducer = (state = initialState, action) => {
  switch (action.type) {
    case ActionTypes.UPDATE_LOADING:
      return { loadState: action.payload };
    default:
      return state;
  }
};

export default loadingReducer;
