import { HiOutlineTrash } from 'react-icons/hi';
import { FoodData } from '../../../pages/FoodDetail';
import FoodIcon from '../../common/foodIcon/FoodIcon';
import Slider from '../slider/Slider';

interface Props {
	foodData: FoodData;
}

function FoodContent({ foodData }: Props) {
	const start = foodData.startDate.split('-');
	const end = foodData.endDate.split('-');

	const sDate = new Date(Number(start[0]), Number(start[1]), Number(start[2]));
	const eDate = new Date(Number(end[0]), Number(end[1]), Number(end[2]));

	const dDay = Math.abs((eDate.getTime() - sDate.getTime()) / (1000 * 60 * 60 * 24));
	let color = 'green';
	if (dDay <= 7) color = 'yellow';
	if (dDay <= 1) color = 'red';
	return (
		<div className="p-6 border-2 stroke component">
			<div className="flex items-center justify-between mb-4">
				<div className="relative flex items-center">
					<div className="relative mr-4">
						<div
							className={`absolute top-[-8px] left-[-8px] bg-${color} w-10 h-5 rounded-lg text-xxs flex justify-center font-bold text-white items-center`}
						>
							D-{dDay > 99 ? '99+' : dDay}
						</div>
						<FoodIcon food={foodData.subCategory} />
					</div>
					<div>
						<div className="text-xs">
							{foodData.category}·{foodData.subCategory}
						</div>
						<div className="text-base font-bold">{foodData.fname}</div>
					</div>
				</div>
				{/* 클릭 시 삭제 요청 */}
				<div className="flex items-center justify-center w-10 h-10 border-2 cursor-pointer rounded-8 stroke">
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
						{start[0]}년{start[1]}월{start[2]}일
					</div>
				</div>
				<div className="flex justify-between text-sm">
					<div>소비기한</div>
					<div className="font-bold">
						{end[0]}년{end[1]}월{end[2]}일
					</div>
				</div>
			</div>
		</div>
	);
}

export default FoodContent;
