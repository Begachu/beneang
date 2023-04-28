import React from 'react';
import Topbar from '../components/common/topbar/Topbar';
import { useAppSelector } from '../hooks/useStore';
import { selectUser } from '../store/modules/user';
import Toggle from '../components/common/toggle/Toggle';

// 설정 화면

function Setting() {
	const userInfo = useAppSelector(selectUser);
	return (
		<div className="px-6 pt-10">
			<Topbar />
			<div className="flex items-center justify-between px-6 py-3 mb-4 border-2 rounded-2xl stroke bg-light/component dark:bg-dark/component text-light/text dark:text-dark/text">
				<div>다크모드</div>
				<Toggle isCheck={false} onState={userInfo.isDark} type="isDark" />
			</div>
			<div className="mb-4 border-2 rounded-2xl stroke bg-light/component dark:bg-dark/component text-light/text dark:text-dark/text">
				<div className="flex items-center justify-between px-6 py-3 border-b-2 stroke">
					<div>알림 받기</div>
					<Toggle isCheck={false} onState={userInfo.isAlarm} type="isAlarm" />
				</div>
				<div className="flex items-center justify-between px-6 py-3">
					<div className={`${userInfo.isAlarm ? 'text-text' : 'text-light/stroke dark:text-dark/stroke'}`}>
						소비기한 임박 식품 알림
					</div>
					<Toggle isCheck={!userInfo.isAlarm} onState={userInfo.isAlarm ? userInfo.isCycle : false} type="isCycle" />
				</div>
				<div className="flex items-center justify-between px-6 py-3">
					<div className={`${userInfo.isAlarm ? 'text-text' : 'text-light/stroke dark:text-dark/stroke'}`}>
						식품 구매주기 알림
					</div>
					<Toggle
						isCheck={!userInfo.isAlarm}
						onState={userInfo.isAlarm ? userInfo.isPurchase : false}
						type="isPurchase"
					/>
				</div>
			</div>
			<div className="px-6 py-3 mb-4 text-left border-2 rounded-2xl text-red stroke bg-light/component dark:bg-dark/component">
				냉장고 초기화하기
			</div>
			<div className="px-6 py-3 text-left border-2 rounded-2xl stroke bg-light/component dark:bg-dark/component text-light/text dark:text-dark/text">
				로그아웃
			</div>
		</div>
	);
}

export default Setting;
