import { useState } from 'react';
import { HiOutlineTrash } from 'react-icons/hi';
import { useMutation } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router';
import moment from 'moment';
import Slider from '../slider/Slider';
import { FoodDetailData } from '../../../types';
import CategoryData from '../../../constants/category.json';
import { FOOD_API, postFoodExpire, postFoodUsed } from '../../../apis/foods';
import Modal from '../../common/modal/Modal';
import DDayFoodIcon from '../../common/foodIcon/DDayFoodIcon';
import { getTodayStr } from '../../../utils/string';

interface Props {
	foodData: FoodDetailData;
}

function FoodContent({ foodData }: Props) {
	const navigate = useNavigate();

	// modal 상태관리
	const [confirmUsed, setConfirmUsed] = useState(false);
	const [confirmDelete, setConfirmDelete] = useState(false);
	const [alertError, setAlertError] = useState(false);

	// api 요청
	const { id } = useParams();
	const mutationUpdate = useMutation([FOOD_API, 'state'], () => postFoodUsed(Number(id)), {
		onSuccess: () => {
			setConfirmUsed(false);
			navigate('/', {
				replace: true,
			});
		},
		onError: () => {
			setConfirmUsed(false);
			setAlertError(true);
		},
	});
	const mutationDelete = useMutation([FOOD_API, 'state'], () => postFoodExpire(Number(id)), {
		onSuccess: () => {
			setConfirmDelete(false);
			navigate('/', {
				replace: true,
			});
		},
		onError: () => {
			setConfirmDelete(false);
			setAlertError(true);
		},
	});

	// D-day 계산
	const today = moment(getTodayStr(), 'YYYY-MM-DD');
	const dDay = moment(foodData.endDate, 'YYYY-MM-DD').diff(today, 'days');

	const start = foodData.startDate.split('-');
	const end = foodData.endDate.split('-');
	const dayCnt = dDay > 99 ? 99 : dDay;
	let color = 'green';
	if (dayCnt <= 7) color = 'yellow';
	if (dayCnt <= 1) color = 'red';

	// 이벤트 핸들러
	const handleUsed = () => {
		setConfirmUsed(true);
	};
	const handleDelete = () => {
		setConfirmDelete(true);
	};
	return (
		<>
			{confirmUsed && (
				<Modal
					mode="confirm"
					size="sm"
					open={confirmUsed}
					onClose={() => setConfirmUsed(false)}
					label="소비 완료"
					submitText="확인"
					onSubmit={() => mutationUpdate.mutate()}
				>
					<div className="text-center">
						<span className="text-green"> {foodData.foodName}</span> 상품을 <br /> 모두 소비 하셨습니까?
					</div>
				</Modal>
			)}
			{confirmDelete && (
				<Modal
					mode="confirm"
					size="sm"
					open={confirmDelete}
					onClose={() => setConfirmDelete(false)}
					label="상품 폐기"
					submitText="폐기"
					onSubmit={() => mutationDelete.mutate()}
				>
					<div className="text-center">
						<span className="text-green"> {foodData.foodName}</span> 상품을 <br /> 모두 폐기 하시겠습니까?
					</div>
				</Modal>
			)}
			{alertError && (
				<Modal
					mode="alert"
					size="sm"
					open={alertError}
					onClose={() => setAlertError(false)}
					label="Error"
					submitText="확인"
					onSubmit={() => setAlertError(false)}
				>
					<div className="text-center">다시 시도해 주세요.</div>
				</Modal>
			)}
			<div className="border stroke component">
				<div className="p-6 ">
					<div className="flex items-center justify-between mb-4">
						<div className="relative flex items-center">
							<div className="relative mr-4">
								<DDayFoodIcon dDay={dDay} icon={foodData.subCategory} />
							</div>
							<div>
								<div className="text-xs text-left">
									{CategoryData.data[foodData.foodCategoryId - 1].category}
									&#32;&#62; {foodData.subCategory}
								</div>
								<div className="text-base font-bold">{foodData.foodName}</div>
							</div>
						</div>
						<div
							className="flex items-center justify-center w-10 h-10 border cursor-pointer bg-light/stroke dark:bg-dark/stroke rounded-8 stroke"
							onClick={handleDelete}
						>
							<HiOutlineTrash className="w-6 h-6 text-light/boldStroke dark:text-dark/boldStroke" />
						</div>
					</div>
					<div className="mb-4">
						<div className="mb-1 text-sm text-left">사용량</div>
						<Slider count={foodData.count} total={foodData.total} />
					</div>
					<div>
						<div className="flex justify-between mb-4 text-sm">
							<div>등록일</div>
							<div className="font-bold">
								{start[0]}년 {start[1]}월 {start[2]}일
							</div>
						</div>
						<div className="flex justify-between text-sm">
							<div>소비기한</div>
							<div className={`font-bold text-${color}`}>
								{end[0]}년 {end[1]}월 {end[2]}일
							</div>
						</div>
					</div>
				</div>
				<div className="flex items-center justify-around w-full h-10 rounded-b-lg bg-green" onClick={handleUsed}>
					<div className="font-bold text-white">사용 완료</div>
				</div>
			</div>
		</>
	);
}

export default FoodContent;
