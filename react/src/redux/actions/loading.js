import { ActionTypes } from "../constants/ActionTypes";

export const updateLoadingAction = loading => ({
  type: ActionTypes.UPDATE_LOADING,
  payload: loading
});
